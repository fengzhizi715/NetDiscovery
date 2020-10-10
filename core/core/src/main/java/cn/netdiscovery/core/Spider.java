package cn.netdiscovery.core;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.config.SpiderConfig;
import cn.netdiscovery.core.domain.Page;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.downloader.file.FileDownloader;
import cn.netdiscovery.core.downloader.urlconnection.UrlConnectionDownloader;
import cn.netdiscovery.core.downloader.vertx.VertxDownloader;
import cn.netdiscovery.core.exception.SpiderException;
import cn.netdiscovery.core.parser.Parser;
import cn.netdiscovery.core.parser.selector.Html;
import cn.netdiscovery.core.parser.selector.Json;
import cn.netdiscovery.core.pipeline.ConsolePipeline;
import cn.netdiscovery.core.pipeline.Pipeline;
import cn.netdiscovery.core.pipeline.PrintRequestPipeline;
import cn.netdiscovery.core.queue.DefaultQueue;
import cn.netdiscovery.core.queue.Queue;
import cn.netdiscovery.core.queue.disruptor.DisruptorQueue;
import cn.netdiscovery.core.rxjava.RetryWithDelay;
import cn.netdiscovery.core.rxjava.transformer.SpiderRunTransformer;
import cn.netdiscovery.core.utils.SpiderUtils;
import cn.netdiscovery.core.utils.Throttle;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Spider可以单独使用，每个Spider只能处理一种Parser，不同的Parser需要不同的Spider
 * 如果需要使用repeatRequest，建议一个Spider只使用一个repeatRequest方法。
 * <p>
 * Created by tony on 2017/12/22.
 */
public class Spider {

    private Logger log = LoggerFactory.getLogger(Spider.class);

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_PAUSE = 2;
    public final static int SPIDER_STATUS_RESUME = 3;
    public final static int SPIDER_STATUS_STOPPED = 4;

    private AtomicInteger stat = new AtomicInteger(SPIDER_STATUS_INIT);

    private String name = "spider";// 爬虫的名字，默认使用spider

    private Parser parser;

    private List<Pipeline> pipelines = new LinkedList<>();

    private Queue queue;

    private boolean autoProxy = false;
    private long initialDelay = 0;
    private int maxRetries = 3; // 重试次数
    private long retryDelayMillis = 1000; // 重试等待的时间
    private long sleepTime = 30000;  // 默认30s
    private long requestSleepTime = 0; // 默认0s
    private boolean autoSleepTime = false;
    private long downloadDelay = 0;  // 默认0s
    private boolean autoDownloadDelay = false;
    private long pipelineDelay = 0;  // 默认0s
    private boolean autoPipelineDelay = false;
    private long domainDelay = 0;  // 默认0s
    private boolean autoDomainDelay = false;

    private volatile boolean pause;
    private CountDownLatch pauseCountDown;
    private ReentrantLock newRequestLock = new ReentrantLock();
    private Condition newRequestCondition = newRequestLock.newCondition();

    private ExecutorService parseThreadPool;
    private ExecutorService pipeLineThreadPool;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Downloader downloader;

    private Spider() {
        String queueType = SpiderConfig.INSTANCE.getQueueType();
        if (Preconditions.isNotBlank(queueType)) {
            switch (queueType) {
                case Constant.QUEUE_TYPE_DEFAULT:
                    this.queue = new DefaultQueue();
                    break;
                case Constant.QUEUE_TYPE_DISRUPTOR:
                    this.queue = new DisruptorQueue();
                    break;
                default:
                    break;
            }
        }

        if (this.queue == null) {
            this.queue = new DefaultQueue();
        }

        initSpiderConfig();
    }

    private Spider(Queue queue) {
        if (queue != null) {
            this.queue = queue;
        } else {
            this.queue = new DefaultQueue();
        }

        initSpiderConfig();
    }

    /**
     * 从 application.conf 中获取配置，并依据这些配置来初始化爬虫
     */
    private void initSpiderConfig() {
        autoProxy = SpiderConfig.INSTANCE.getAutoProxy();
        initialDelay = SpiderConfig.INSTANCE.getInitialDelay();
        maxRetries = SpiderConfig.INSTANCE.getMaxRetries();
        retryDelayMillis = SpiderConfig.INSTANCE.getRetryDelayMillis();

        requestSleepTime = SpiderConfig.INSTANCE.getSleepTime();
        autoSleepTime = SpiderConfig.INSTANCE.getAutoSleepTime();
        downloadDelay = SpiderConfig.INSTANCE.getDownloadDelay();
        autoDownloadDelay = SpiderConfig.INSTANCE.getAutoDownloadDelay();
        domainDelay = SpiderConfig.INSTANCE.getDomainDelay();
        autoDomainDelay = SpiderConfig.INSTANCE.getAutoDomainDelay();

        pipelineDelay = SpiderConfig.INSTANCE.getPipelineDelay();
        autoPipelineDelay = SpiderConfig.INSTANCE.getAutoPipelineDelay();

        String downloaderType = SpiderConfig.INSTANCE.getDownloaderType();

        if (Preconditions.isNotBlank(downloaderType)) {
            switch (downloaderType) {
                case Constant.DOWNLOAD_TYPE_VERTX:
                    this.downloader = new VertxDownloader();
                    break;
                case Constant.DOWNLOAD_TYPE_URL_CONNECTION:
                    this.downloader = new UrlConnectionDownloader();
                    break;
                case Constant.DOWNLOAD_TYPE_FILE:
                    this.downloader = new FileDownloader();
                    break;
                default:
                    break;
            }
        }

        if (SpiderConfig.INSTANCE.getUsePrintRequestPipeline()) {
            this.pipelines.add(new PrintRequestPipeline()); // 默认使用 PrintRequestPipeline
        }
        if (SpiderConfig.INSTANCE.getUseConsolePipeline()) {
            this.pipelines.add(new ConsolePipeline()); // 默认使用 ConsolePipeline
        }
    }

    public static Spider create() {
        return new Spider();
    }

    public static Spider create(Queue queue) {
        return queue != null ? new Spider(queue) : new Spider();
    }

    public Spider name(String name) {
        checkIfRunning();

        if (Preconditions.isNotBlank(name)) {
            this.name = name;
        }

        return this;
    }

    public Spider url(Charset charset, String... urls) {
        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(urls)
                    .stream()
                    .forEach(url -> pushToQueue(url,charset));

            signalNewRequest();
        }

        return this;
    }

    public Spider url(String... urls) {
        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(urls)
                    .stream()
                    .forEach(url -> pushToQueue(url,null));

            signalNewRequest();
        }

        return this;
    }

    public Spider url(Charset charset, List<String> urls) {
        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach(url -> pushToQueue(url,charset));

            signalNewRequest();
        }

        return this;
    }

    public Spider url(List<String> urls) {
        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach(url -> pushToQueue(url,null));

            signalNewRequest();
        }

        return this;
    }

    private void pushToQueue(String url,Charset charset) {
        Request request = new Request(url, name);
        if (charset!=null) {
            request.charset(charset.name());
        }
        if (autoSleepTime) {
            request.autoSleepTime();
        } else {
            request.sleep(requestSleepTime);
        }
        if (autoDownloadDelay) {
            request.autoDownloadDelay();
        } else {
            request.downloadDelay(downloadDelay);
        }
        if (autoDomainDelay) {
            request.autoDomainDelay();
        } else {
            request.domainDelay(domainDelay);
        }
        queue.push(request);
    }

    public Spider request(Request... requests) {
        checkIfRunning();

        if (Preconditions.isNotBlank(requests)) {

            Arrays.asList(requests)
                    .stream()
                    .forEach(request -> {
                        queue.push(request.spiderName(name));
                    });

            signalNewRequest();
        }

        return this;
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     *
     * @param period 每隔一定的时间提交一次request
     * @param url
     * @return
     */
    public Spider repeatRequest(long period, String url) {

        return repeatRequest(period, url, Constant.UTF_8);
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     *
     * @param period
     * @param url
     * @param charset 字符集
     * @return
     */
    public Spider repeatRequest(long period, String url, String charset) {
        checkIfRunning();

        compositeDisposable
                .add(Flowable.interval(period, TimeUnit.MILLISECONDS)
                        .onBackpressureBuffer()
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {

                                if (!pause) {
                                    Request request = new Request(url);
                                    request.checkDuplicate(false);
                                    request.spiderName(name);
                                    request.sleep(period); // 使用 repeatRequest() 时，autoSleepTime 属性可以不必关注
                                    request.charset(charset);
                                    if (autoDownloadDelay) {
                                        request.autoDownloadDelay();
                                    } else {
                                        request.downloadDelay(downloadDelay);
                                    }
                                    if (autoDomainDelay) {
                                        request.autoDomainDelay();
                                    } else {
                                        request.domainDelay(domainDelay);
                                    }
                                    queue.push(request);
                                    
                                    signalNewRequest();
                                }

                            }
                        }));

        return this;
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period
     * @param request
     * @return
     */
    public Spider repeatRequest(long period, Request request) {
        checkIfRunning();

        if (request!=null) {

            compositeDisposable
                    .add(Flowable.interval(period, TimeUnit.MILLISECONDS)
                            .onBackpressureBuffer()
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {

                                    if (!pause) {

                                        if (request.getSleepTime()==0 || request.getSleepTime()!=period) {
                                            request.sleep(period); // 使用 repeatRequest() 时，autoSleepTime 属性可以不必关注
                                        }

                                        if (request.getDownloadDelay()==0) {
                                            if (autoDownloadDelay) {
                                                request.autoDownloadDelay();
                                            } else {
                                                request.downloadDelay(downloadDelay);
                                            }
                                        }

                                        if (request.getDomainDelay()==0) {
                                            if (autoDomainDelay) {
                                                request.autoDomainDelay();
                                            } else {
                                                request.domainDelay(domainDelay);
                                            }
                                        }

                                        request.spiderName(name);
                                        queue.push(request);

                                        signalNewRequest();
                                    }

                                }
                            }));
        }

        return this;
    }

    public Spider initialDelay(long initialDelay) {
        checkIfRunning();

        if (initialDelay > 0) {
            this.initialDelay = initialDelay;
        }

        return this;
    }

    public Spider maxRetries(int maxRetries) {
        checkIfRunning();

        if (maxRetries > 0) {
            this.maxRetries = maxRetries;
        }

        return this;
    }

    public Spider retryDelayMillis(long retryDelayMillis) {
        checkIfRunning();

        if (retryDelayMillis > 0) {
            this.retryDelayMillis = retryDelayMillis;
        }

        return this;
    }

    public Spider sleepTime(long sleepTime) {

        checkIfRunning();

        if (sleepTime > 0) {
            this.sleepTime = sleepTime;
        }

        return this;
    }

    public Spider downloader(Downloader downloader) {
        checkIfRunning();

        if (downloader != null) {
            this.downloader = downloader;
        }

        return this;
    }

    public Spider parser(Parser parser) {
        checkIfRunning();

        if (parser != null) {
            this.parser = parser;
        }

        return this;
    }

    public Spider pipeline(Pipeline pipeline) {
        checkIfRunning();

        if (pipeline != null) {

            if (pipeline.getPipelineDelay()==0) {
                if (autoPipelineDelay) {
                    pipelineDelay = RandomUtils.nextLong(1000,6000);
                }
                pipeline.setPipelineDelay(pipelineDelay);
            }

            this.pipelines.add(pipeline);
        }

        return this;
    }

    public Spider clearPipeline() {
        checkIfRunning();
        this.pipelines.clear();
        return this;
    }

    /**
     * 自动获取代理，从代理池组件中获取代理
     *
     * @return
     */
    public Spider autoProxy() {
        return autoProxy(true);
    }

    /**
     * 是否自动获取代理，如果是的话可以从代理池组件中获取代理
     *
     * @param autoProxy
     * @return
     */
    public Spider autoProxy(boolean autoProxy) {
        checkIfRunning();
        this.autoProxy = autoProxy;
        return this;
    }

    public Spider parseThreadPool(ExecutorService parseThreadPool) {
        this.parseThreadPool = parseThreadPool;
        return this;
    }

    public Spider pipeLineThreadPool(ExecutorService pipeLineThreadPool) {
        this.pipeLineThreadPool = pipeLineThreadPool;
        return this;
    }

    private void waitNewRequest() {
        newRequestLock.lock();

        try {
            newRequestCondition.await(sleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("waitNewRequest - interrupted, error {}", e);
        } finally {
            newRequestLock.unlock();
        }
    }

    public void signalNewRequest() {
        newRequestLock.lock();

        try {
            newRequestCondition.signalAll();
        } finally {
            newRequestLock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    public Queue getQueue() {
        return queue;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public void run() {
        checkRunningStat();

        initialDelay();

        if (downloader == null) { // 如果downloader为空，则使用默认的VertxDownloader
            downloader = new VertxDownloader();
        }

        log.info("Spider {} started!",getName());

        while (getSpiderStatus() != SPIDER_STATUS_STOPPED) {

            //暂停抓取
            if (pause && pauseCountDown!=null) {
                try {
                    this.pauseCountDown.await();
                } catch (InterruptedException e) {
                    log.error("can't pause : ", e);
                }

                initialDelay();
            }

            // 从消息队列中取出request
            final Request request = queue.poll(name);

            if (request == null) {
                waitNewRequest();
            } else {
                if (request.getSleepTime() > 0) {
                    try {
                        Thread.sleep(request.getSleepTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Throttle.getInsatance().wait(request);

                // 如果autoProxy打开并且request.getProxy()==null时，则从ProxyPool中取Proxy
                if (autoProxy && request.getProxy() == null) {

                    Proxy proxy = ProxyPool.getProxy();

                    if (proxy != null && SpiderUtils.checkProxy(proxy)) {
                        request.proxy(proxy);
                    }
                }

                // request请求之前的处理
                if (request.getBeforeRequest() != null) {
                    request.getBeforeRequest().process(request);
                }

                // request正在处理
                downloader.download(request)
                        .retryWhen(new RetryWithDelay(maxRetries, retryDelayMillis, request)) // 对网络请求的重试机制
                        .map(new Function<Response, Page>() {

                            @Override
                            public Page apply(Response response) throws Exception {

                                Page page = new Page();
                                page.addRequest(request);
                                page.setUrl(request.getUrl());
                                page.setStatusCode(response.getStatusCode());

                                if (SpiderUtils.isTextType(response.getContentType())) { // text/html

                                    page.setHtml(new Html(response.getContent()));
                                } else if (SpiderUtils.isApplicationJSONType(response.getContentType())) { // application/json

                                    // 将json字符串转化成Json对象，放入Page的"RESPONSE_JSON"字段。之所以转换成Json对象，是因为Json提供了toObject()，可以转换成具体的class。
                                    page.putField(Constant.RESPONSE_JSON, new Json(new String(response.getContent())));
                                } else if (SpiderUtils.isApplicationJSONPType(response.getContentType())) { // application/javascript

                                    // 转换成字符串，放入Page的"RESPONSE_JSONP"字段。
                                    // 由于是jsonp，需要开发者在Pipeline中自行去掉字符串前后的内容，这样就可以变成json字符串了。
                                    page.putField(Constant.RESPONSE_JSONP, new String(response.getContent()));
                                } else {

                                    page.putField(Constant.RESPONSE_RAW, response.getIs()); // 默认情况，保存InputStream
                                }

                                return page;
                            }
                        })
                        .compose(new SpiderRunTransformer(parseThreadPool))
                        .map(new Function<Page, Page>() {

                            @Override
                            public Page apply(Page page) throws Exception {

                                if (parser != null) {

                                    parser.process(page);
                                }

                                return page;
                            }
                        })
                        .compose(new SpiderRunTransformer(pipeLineThreadPool))
                        .map(new Function<Page, Page>() {

                            @Override
                            public Page apply(Page page) throws Exception {

                                if (!page.getResultItems().getSkip() && Preconditions.isNotBlank(pipelines)) {

                                    pipelines.stream()
                                            .forEach(pipeline -> {

                                                if (pipeline.getPipelineDelay()>0) {

                                                    // Pipeline Delay
                                                    Observable.just("pipeline delay").delay(pipeline.getPipelineDelay(),TimeUnit.MILLISECONDS).blockingFirst();
                                                }

                                                pipeline.process(page.getResultItems());
                                            });
                                }

                                return page;
                            }
                        })
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<Page>() {

                            @Override
                            public void accept(Page page) throws Exception {

                                log.info(page.getUrl());

                                if (request.getAfterRequest() != null) {

                                    request.getAfterRequest().process(page);
                                }

                                signalNewRequest();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                log.error(throwable.getMessage(), throwable);
                            }
                        });
            }
        }
        stopSpider(downloader); // 爬虫停止
    }

    private void checkIfRunning() {
        if (getSpiderStatus() == SPIDER_STATUS_RUNNING) {
            throw new SpiderException(String.format("Spider %s is already running!", name));
        }
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = getSpiderStatus();
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw new SpiderException(String.format("Spider %s is already running!", name));
            }
            if (stat.compareAndSet(statNow, SPIDER_STATUS_RUNNING)) {
                break;
            }
        }
    }

    public int getSpiderStatus() {

        return stat.get();
    }

    private void stopSpider(Downloader downloader) {

        IOUtils.closeQuietly(downloader);

        stop();
    }

    public void stop() {
        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            log.info("Spider {} stop success!", name);
        }
    }

    /**
     * 如果爬虫使用了repeatRequest
     * 爬虫需要停止时，需要使用该方法
     */
    public void forceStop() {
        compositeDisposable.clear();

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            log.info("Spider {} force stop success!", name);
        }
    }

    /**
     * 爬虫暂停，当前正在抓取的请求会继续抓取完成，之后的请求会等到resume的调用才继续抓取
     */
    public void pause() {
        this.pauseCountDown = new CountDownLatch(1);
        this.pause = true;
        stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_PAUSE);
    }

    /**
     * 爬虫重新开始
     */
    public void resume() {
        if (stat.get() == SPIDER_STATUS_PAUSE
                && this.pauseCountDown!=null) {

            this.pauseCountDown.countDown();
            this.pause = false;
            stat.compareAndSet(SPIDER_STATUS_PAUSE, SPIDER_STATUS_RUNNING);
        }
    }

    private void initialDelay() {
        if (initialDelay > 0) {

            try {
                Thread.sleep(initialDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}