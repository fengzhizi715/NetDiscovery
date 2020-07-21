package cn.netdiscovery.core;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.bean.SpiderBean;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;
import cn.netdiscovery.core.domain.response.JobsResponse;
import cn.netdiscovery.core.domain.response.SpiderResponse;
import cn.netdiscovery.core.domain.response.SpiderStatusResponse;
import cn.netdiscovery.core.domain.response.SpidersResponse;
import cn.netdiscovery.core.utils.SerializableUtils;
import cn.netdiscovery.core.vertx.VertxManager;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.netdiscovery.core.config.Constant.*;
import static cn.netdiscovery.core.domain.response.HttpResponse.Ok;

/**
 * SpiderEngine 对外提供的 http 接口
 *
 * Created by tony on 2019-08-03.
 */
public class RouterHandler {

    private Map<String, Spider> spiders;
    private Router router;
    private Map<String, SpiderJobBean> jobs;
    private boolean useMonitor;

    public RouterHandler(Map<String, Spider> spiders,Map<String, SpiderJobBean> jobs,Router router,boolean useMonitor) {

        this.spiders = spiders;
        this.jobs = jobs;
        this.router = router;
        this.useMonitor = useMonitor;
    }

    public void route() {

        // 检测 SpiderEngine 的健康状况
        router.route(Constant.ROUTER_HEALTH).handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

            response.end(SerializableUtils.toJson(Ok));
        });

        router.route(Constant.ROUTER_METRICS).handler(PrometheusScrapingHandler.create());

        if (Preconditions.isNotBlank(spiders)) {

            // 显示容器下所有爬虫的信息
            router.route(Constant.ROUTER_SPIDERS).handler(routingContext -> {

                HttpServerResponse response = routingContext.response();
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

                List<SpiderBean> list = new ArrayList<>();

                Spider spider = null;
                SpiderBean entity = null;

                for (Map.Entry<String, Spider> entry : spiders.entrySet()) {

                    spider = entry.getValue();

                    entity = new SpiderBean();
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
                spidersResponse.setCode(OK_STATUS_CODE);
                spidersResponse.setMessage(SUCCESS);
                spidersResponse.setData(list);

                // 写入响应并结束处理
                response.end(SerializableUtils.toJson(spidersResponse));
            });

            // 根据爬虫的名称获取爬虫的详情
            router.route(Constant.ROUTER_SPIDER_DETAIL).handler(routingContext -> {

                HttpServerResponse response = routingContext.response();
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

                String spiderName = routingContext.pathParam("spiderName");

                if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName)!=null) {

                    Spider spider = spiders.get(spiderName);

                    SpiderBean entity = new SpiderBean();
                    entity.setSpiderName(spiderName);
                    entity.setSpiderStatus(spider.getSpiderStatus());
                    entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spiderName));
                    entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spiderName));
                    entity.setConsumedRequestSize(entity.getTotalRequestSize()-entity.getLeftRequestSize());
                    entity.setQueueType(spider.getQueue().getClass().getSimpleName());
                    entity.setDownloaderType(spider.getDownloader().getClass().getSimpleName());

                    SpiderResponse spiderResponse = new SpiderResponse();
                    spiderResponse.setCode(OK_STATUS_CODE);
                    spiderResponse.setMessage(SUCCESS);
                    spiderResponse.setData(entity);

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderResponse));
                } else {

                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound));
                }
            });

            // 修改单个爬虫的状态
            router.post(Constant.ROUTER_SPIDER_STATUS).handler(routingContext -> {

                HttpServerResponse response = routingContext.response();
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

                String spiderName = routingContext.pathParam("spiderName");

                if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName)!=null) {

                    JsonObject json = routingContext.getBodyAsJson();
                    SpiderStatusResponse spiderStatusResponse = null;

                    Spider spider = spiders.get(spiderName);

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

                    spiderStatusResponse.setCode(OK_STATUS_CODE);
                    spiderStatusResponse.setMessage(SUCCESS);

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderStatusResponse));
                } else {
                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound));
                }

            });

            // 添加新的url任务到某个正在运行中的爬虫
            router.post(Constant.ROUTER_SPIDER_PUSH).handler(routingContext -> {

                HttpServerResponse response = routingContext.response();
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

                String spiderName = routingContext.pathParam("spiderName");

                if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName)!=null) {

                    JsonObject json = routingContext.getBodyAsJson();

                    String url = json.getString("url");

                    Spider spider = spiders.get(spiderName);
                    spider.getQueue().pushToRunninSpider(url,spider);

                    response.end(SerializableUtils.toJson(new cn.netdiscovery.core.domain.response.HttpResponse("待抓取的url已经放入queue中")));
                } else {
                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound));
                }

            });

            // 显示容器内所有爬虫的定时任务
            router.route(Constant.ROUTER_JOBS).handler(routingContext -> {

                HttpServerResponse response = routingContext.response();
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);

                List<SpiderJobBean> list = new ArrayList<>();

                list.addAll(jobs.values());

                JobsResponse jobsResponse = new JobsResponse();
                jobsResponse.setCode(OK_STATUS_CODE);
                jobsResponse.setMessage(SUCCESS);
                jobsResponse.setData(list);

                // 写入响应并结束处理
                response.end(SerializableUtils.toJson(jobsResponse));
            });

            if (useMonitor) { // 是否使用 agent

                // The web server handler
                router.route().handler(StaticHandler.create().setCachingEnabled(false));

                // The proxy handler
                WebClient client = WebClient.create(VertxManager.getVertx());

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
                                    .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                                    .end(result.body());
                        } else {
                            ctx.fail(ar.cause());
                        }
                    });
                });
            }
        }
    }
}
