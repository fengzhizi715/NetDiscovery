package cn.netdiscovery.core;

import cn.netdiscovery.core.config.Configuration;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.JobEntity;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.SpiderEntity;
import cn.netdiscovery.core.domain.response.SpiderResponse;
import cn.netdiscovery.core.domain.response.SpiderStatusResponse;
import cn.netdiscovery.core.domain.response.SpidersResponse;
import cn.netdiscovery.core.quartz.QuartzManager;
import cn.netdiscovery.core.quartz.SpiderJob;
import cn.netdiscovery.core.queue.Queue;

import cn.netdiscovery.core.utils.BooleanUtils;
import cn.netdiscovery.core.utils.NumberUtils;
import cn.netdiscovery.core.utils.SerializableUtils;
import cn.netdiscovery.core.utils.UserAgent;
import cn.netdiscovery.core.vertx.VertxUtils;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.netdiscovery.core.config.Constant.JOB_GROUP_NAME;
import static cn.netdiscovery.core.config.Constant.JOB_NAME;
import static cn.netdiscovery.core.config.Constant.TRIGGER_GROUP_NAME;
import static cn.netdiscovery.core.config.Constant.TRIGGER_NAME;

/**
 * 可以管理多个Spider的容器
 * Created by tony on 2018/1/2.
 */
@Slf4j
public class SpiderEngine {

    @Getter
    private Queue queue;

    private HttpServer server;

    private boolean useMonitor = false;

    private RegisterConsumer registerConsumer;

    private int defaultHttpdPort = 8715; // SpiderEngine 默认的端口号

    private AtomicInteger count = new AtomicInteger(0);

    private Map<String, Spider> spiders = new ConcurrentHashMap<>();

    private Map<String, JobEntity> jobs = new ConcurrentHashMap<>();

    private SpiderEngine() {

        this(null);
    }

    private SpiderEngine(Queue queue) {

        this.queue = queue;

        initSpiderEngine();
    }

    /**
     * 初始化爬虫引擎，加载ua列表
     */
    private void initSpiderEngine() {

        String[] uaList = Constant.uaList;

        if (Preconditions.isNotBlank(uaList)) {

            Arrays.asList(uaList)
                    .parallelStream()
                    .forEach(name -> {

                        InputStream input = null;

                        try {
                            input = this.getClass().getResourceAsStream(name);
                            String inputString = IOUtils.inputStream2String(input); // input 流无须关闭，inputStream2String()方法里已经做了关闭流的操作
                            if (Preconditions.isNotBlank(inputString)) {
                                String[] ss = inputString.split("\r\n");
                                if (ss.length > 0) {

                                    Arrays.asList(ss).forEach(ua -> UserAgent.uas.add(ua));
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        try {
            defaultHttpdPort = NumberUtils.toInt(Configuration.getConfig("spiderEngine.config.port"));
            useMonitor = BooleanUtils.toBoolean(Configuration.getConfig("spider.config.autoProxy"));
        } catch (ClassCastException e) {
            defaultHttpdPort = 8715;
            useMonitor = false;
        }
    }

    public static SpiderEngine create() {

        return new SpiderEngine();
    }

    public static SpiderEngine create(Queue queue) {

        return new SpiderEngine(queue);
    }

    public SpiderEngine proxyList(List<Proxy> proxies) {

        ProxyPool.addProxyList(proxies);
        return this;
    }

    public SpiderEngine setUseMonitor(boolean useMonitor) {

        this.useMonitor = useMonitor;
        return this;
    }

    /**
     * 添加爬虫到SpiderEngine，由SpiderEngine来管理
     *
     * @param spider
     * @return
     */
    public SpiderEngine addSpider(Spider spider) {

        if (spider != null && !spiders.containsKey(spider.getName())) {

            spiders.put(spider.getName(), spider);
        }
        return this;
    }

    /**
     * 在SpiderEngine中创建一个爬虫，使用SpiderEngine的Queue
     *
     * @param name
     * @return Spider
     */
    public Spider createSpider(String name) {

        if (!spiders.containsKey(name)) {

            Spider spider = Spider.create(this.getQueue()).name(name);
            spiders.put(name, spider);
            return spider;
        }

        return null;
    }

    /**
     * 对各个爬虫的状态进行监测，并返回json格式。
     * 如果要使用此方法，须放在run()之前
     * 采用默认的端口号
     * @return
     */
    public SpiderEngine httpd() {

        return httpd(defaultHttpdPort);
    }

    /**
     * 对各个爬虫的状态进行监测，并返回json格式。
     * 如果要使用此方法，须放在run()之前
     *
     * @param port
     */
    public SpiderEngine httpd(int port) {

        server = VertxUtils.getVertx().createHttpServer();

        Router router = Router.router(VertxUtils.getVertx());
        router.route().handler(BodyHandler.create());

        if (Preconditions.isNotBlank(spiders)) {

            for (Map.Entry<String, Spider> entry : spiders.entrySet()) {

                final Spider spider = entry.getValue();

                router.route("/netdiscovery/spider/" + spider.getName()).handler(routingContext -> {

                    // 所有的请求都会调用这个处理器处理
                    HttpServerResponse response = routingContext.response();
                    response.putHeader(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);

                    SpiderEntity entity = new SpiderEntity();
                    entity.setSpiderName(spider.getName());
                    entity.setSpiderStatus(spider.getSpiderStatus());
                    entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spider.getName()));
                    entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spider.getName()));
                    entity.setConsumedRequestSize(entity.getTotalRequestSize()-entity.getLeftRequestSize());
                    entity.setQueueType(spider.getQueue().getClass().getSimpleName());
                    entity.setDownloaderType(spider.getDownloader().getClass().getSimpleName());

                    SpiderResponse spiderResponse = new SpiderResponse();
                    spiderResponse.setCode(Constant.OK_STATUS_CODE);
                    spiderResponse.setMessage(Constant.SUCCESS);
                    spiderResponse.setData(entity);

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderResponse));
                });

                router.post("/netdiscovery/spider/" + spider.getName() + "/status").handler(routingContext -> {

                    // 所有的请求都会调用这个处理器处理
                    HttpServerResponse response = routingContext.response();
                    response.putHeader(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);

                    JsonObject json = routingContext.getBodyAsJson();

                    SpiderStatusResponse spiderStatusResponse = null;

                    if (json != null) {

                        int status = json.getInteger("status");

                        spiderStatusResponse = new SpiderStatusResponse();

                        switch (status) {

                            case Spider.SPIDER_STATUS_PAUSE: {
                                spider.pause();
                                spiderStatusResponse.setData(String.format("SpiderEngine pause Spider %s success", spider.getName()));
                                break;
                            }

                            case Spider.SPIDER_STATUS_RESUME: {
                                spider.resume();
                                spiderStatusResponse.setData(String.format("SpiderEngine resume Spider %s success", spider.getName()));
                                break;
                            }

                            case Spider.SPIDER_STATUS_STOPPED: {
                                spider.forceStop();
                                spiderStatusResponse.setData(String.format("SpiderEngine stop Spider %s success", spider.getName()));
                                break;
                            }

                            default:
                                break;
                        }
                    }

                    spiderStatusResponse.setCode(Constant.OK_STATUS_CODE);
                    spiderStatusResponse.setMessage(Constant.SUCCESS);

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderStatusResponse));
                });
            }

            router.route("/netdiscovery/spiders/").handler(routingContext -> {

                // 所有的请求都会调用这个处理器处理
                HttpServerResponse response = routingContext.response();
                response.putHeader(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);

                List<SpiderEntity> list = new ArrayList<>();

                Spider spider = null;
                SpiderEntity entity = null;

                for (Map.Entry<String, Spider> entry : spiders.entrySet()) {

                    spider = entry.getValue();

                    entity = new SpiderEntity();
                    entity.setSpiderName(spider.getName());
                    entity.setSpiderStatus(spider.getSpiderStatus());
                    entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spider.getName()));
                    entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spider.getName()));
                    entity.setConsumedRequestSize(entity.getTotalRequestSize()-entity.getLeftRequestSize());
                    entity.setQueueType(spider.getQueue().getClass().getSimpleName());
                    entity.setDownloaderType(spider.getDownloader().getClass().getSimpleName());
                    list.add(entity);
                }

                SpidersResponse spidersResponse = new SpidersResponse();
                spidersResponse.setCode(Constant.OK_STATUS_CODE);
                spidersResponse.setMessage(Constant.SUCCESS);
                spidersResponse.setData(list);

                // 写入响应并结束处理
                response.end(SerializableUtils.toJson(spidersResponse));
            });

            if (useMonitor) {

                // The web server handler
                router.route().handler(StaticHandler.create().setCachingEnabled(false));

                // The proxy handler
                WebClient client = WebClient.create(VertxUtils.getVertx());

                InetAddress localhost = null;
                try {
                    localhost = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                HttpRequest<Buffer> get = client.get(8081, localhost.getHostAddress(), "/netdiscovery/dashboard/");
                router.get("/dashboard").handler(ctx -> {
                    get.send(ar -> {
                        if (ar.succeeded()) {
                            HttpResponse<Buffer> result = ar.result();
                            ctx.response()
                                    .setStatusCode(result.statusCode())
                                    .putHeader("Content-Type", "application/json")
                                    .end(result.body());
                        } else {
                            ctx.fail(ar.cause());
                        }
                    });
                });
            }
        }

        server.requestHandler(router::accept).listen(port);

        return this;
    }

    /**
     * 注册 Vert.x eventBus 的消费者
     * @param registerConsumer
     * @return
     */
    public SpiderEngine registerConsumers(RegisterConsumer registerConsumer) {

        this.registerConsumer = registerConsumer;
        return this;
    }

    /**
     * 关闭HttpServer
     */
    public void closeHttpServer() {

        if (server != null) {

            server.close();
        }
    }

    /**
     * 启动SpiderEngine中所有的spider，让每个爬虫并行运行起来。
     *
     */
    public void run() {

        if (Preconditions.isNotBlank(spiders)) {

            if (registerConsumer!=null) {
                registerConsumer.process();
            }

            Flowable.fromIterable(spiders.values())
                    .parallel(spiders.values().size())
                    .runOn(Schedulers.io())
                    .map(new Function<Spider, Spider>() {

                        @Override
                        public Spider apply(Spider spider) throws Exception {

                            spider.run();

                            return spider;
                        }
                    })
                    .sequential()
                    .subscribe();

            Runtime.getRuntime().addShutdownHook(new Thread(()-> {
                log.info("stop all spiders");
                stopSpiders();
                QuartzManager.shutdownJobs();
            }));
        }
    }

    /**
     * 基于爬虫的名字，从SpiderEngine中获取爬虫
     *
     * @param name
     */
    public Spider getSpider(String name) {

        return spiders.get(name);
    }

    /**
     * 停止某个爬虫程序
     *
     * @param name
     */
    public void stopSpider(String name) {

        Spider spider = spiders.get(name);

        if (spider != null) {

            spider.stop();
        }
    }

    /**
     * 停止所有的爬虫程序
     */
    public void stopSpiders() {

        if (Preconditions.isNotBlank(spiders)) {

            spiders.forEach((s, spider) -> spider.stop());
        }
    }

    /**
     * 给 Spider 发起定时任务
     * @param spiderName
     * @param request
     * @param cron cron表达式
     */
    public JobEntity addJob(String spiderName, Request request, String cron) {

        Spider spider = spiders.get(spiderName);

        if (spider!=null){
            String jobName = JOB_NAME + count.incrementAndGet();

            JobEntity jobEntity = new JobEntity();
            jobEntity.setJobName(jobName);
            jobEntity.setJobGroupName(JOB_GROUP_NAME);
            jobEntity.setTriggerName(TRIGGER_NAME);
            jobEntity.setTriggerGroupName(TRIGGER_GROUP_NAME);
            jobEntity.setCron(cron);
            jobEntity.setUrl(request.getUrl());

            jobs.put(jobName,jobEntity);
            QuartzManager.addJob(jobEntity.getJobName(), jobEntity.getJobGroupName(), jobEntity.getTriggerName(), jobEntity.getTriggerGroupName(), SpiderJob.class, cron, spider, request);

            return jobEntity;
        }

        return null;
    }

    /**
     * 给 Spider 发起定时任务
     * @param spiderName
     * @param url
     * @param cron cron表达式
     */
    public JobEntity addJob(String spiderName, String url, String cron) {

        Spider spider = spiders.get(spiderName);

        if (spider!=null){

            Request request = new Request(url,spiderName);
            String jobName = JOB_NAME + count.incrementAndGet();

            JobEntity jobEntity = new JobEntity();
            jobEntity.setJobName(jobName);
            jobEntity.setJobGroupName(JOB_GROUP_NAME);
            jobEntity.setTriggerName(TRIGGER_NAME);
            jobEntity.setTriggerGroupName(TRIGGER_GROUP_NAME);
            jobEntity.setCron(cron);
            jobEntity.setUrl(request.getUrl());

            jobs.put(jobName,jobEntity);
            QuartzManager.addJob(jobEntity.getJobName(), jobEntity.getJobGroupName(), jobEntity.getTriggerName(), jobEntity.getTriggerGroupName(), SpiderJob.class, cron, spider, request);

            return jobEntity;
        }

        return null;
    }

    /**
     * 注册 Vert.x eventBus 的消费者
     */
    @FunctionalInterface
    public interface RegisterConsumer {

        void process();
    }
}
