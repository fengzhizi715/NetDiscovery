package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.core.downloader.vertx.VertxDownloader;
import com.cv4j.netdiscovery.core.exception.SpiderException;
import com.cv4j.netdiscovery.core.parser.Parser;
import com.cv4j.netdiscovery.core.parser.selector.Html;
import com.cv4j.netdiscovery.core.parser.selector.Json;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.cv4j.netdiscovery.core.utils.RetryWithDelay;
import com.cv4j.netdiscovery.core.utils.Utils;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spider可以单独使用，每个Spider只能处理一种Parser，不同的Parser需要不同的Spider
 * 如果需要使用repeatRequest，建议一个Spider只使用一个repeatRequest方法。
 * <p>
 * Created by tony on 2017/12/22.
 */
@Slf4j
public class Spider {

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_PAUSE = 2;
    public final static int SPIDER_STATUS_RESUME= 3;
    public final static int SPIDER_STATUS_STOPPED = 4;

    protected AtomicInteger stat = new AtomicInteger(SPIDER_STATUS_INIT);

    @Getter
    private String name = "spider";// 爬虫的名字，默认使用spider

    private Parser parser;

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    @Getter
    private Queue queue;

    private boolean autoProxy = false;

    private long initialDelay = 0;

    private int maxRetries = 3;

    private int retryDelayMillis = 1000;

    private volatile boolean pause;
    private CountDownLatch pauseCountDown;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Getter
    private Downloader downloader;

    private Spider() {
        this(new DefaultQueue());
    }

    private Spider(Queue queue) {

        if (queue!=null) {
            this.queue = queue;
        } else {
            this.queue = new DefaultQueue();
        }

        downloader = new VertxDownloader();
    }

    public static Spider create() {

        return new Spider();
    }

    public static Spider create(Queue queue) {

        return queue!=null?new Spider(queue):new Spider();
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
                    .forEach(url -> {
                        Request request = new Request(url, name);
                        request.charset(charset.name());
                        queue.push(request);
                    });
        }

        return this;
    }

    public Spider url(String... urls) {

        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(urls)
                    .stream()
                    .forEach(url -> queue.push(new Request(url, name)));
        }

        return this;
    }

    public Spider url(Charset charset, List<String> urls) {

        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach(url -> {
                Request request = new Request(url, name);
                request.charset(charset.name());
                queue.push(request);
            });
        }

        return this;
    }

    public Spider url(List<String> urls) {

        checkIfRunning();

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach(url -> queue.push(new Request(url, name)));
        }

        return this;
    }

    public Spider request(Request... requests) {

        checkIfRunning();

        if (Preconditions.isNotBlank(requests)) {

            Arrays.asList(requests)
                    .stream()
                    .forEach(request -> queue.push(request.spiderName(name)));
        }

        return this;
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period 每隔一定的时间提交一次request
     * @param url
     * @return
     */
    public Spider repeatRequest(long period, String url) {

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
                            request.sleep(period);
                            queue.push(request);
                        }

                    }
                }));

        return this;
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period
     * @param url
     * @param charset 字符集
     * @return
     */
    public Spider repeatRequest(long period, String url,String charset) {

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
                                    request.sleep(period);
                                    request.charset(charset);
                                    queue.push(request);
                                }

                            }
                        }));

        return this;
    }

    public Spider initialDelay(long initialDelay) {

        checkIfRunning();

        if (initialDelay>0) {
            this.initialDelay = initialDelay;
        }

        return this;
    }

    public Spider maxRetries(int maxRetries) {

        checkIfRunning();

        if (maxRetries>0) {
            this.maxRetries = maxRetries;
        }

        return this;
    }

    public Spider retryDelayMillis(int retryDelayMillis) {

        checkIfRunning();

        if (retryDelayMillis>0) {
            this.retryDelayMillis = retryDelayMillis;
        }

        return this;
    }

    public Spider downloader(Downloader downloader) {

        checkIfRunning();

        if (downloader!=null) {
            this.downloader = downloader;
        }

        return this;
    }

    public Spider parser(Parser parser) {

        checkIfRunning();

        if (parser!=null) {
            this.parser = parser;
        }

        return this;
    }

    public Spider pipeline(Pipeline pipeline) {

        checkIfRunning();

        if (pipeline!=null) {
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
     * @return
     */
    public Spider autoProxy() {

        return autoProxy(true);
    }

    /**
     * 是否自动获取代理，如果是的话可以从代理池组件中获取代理
     * @param autoProxy
     * @return
     */
    public Spider autoProxy(boolean autoProxy) {

        checkIfRunning();
        this.autoProxy = autoProxy;
        return this;
    }

    public void run() {

        checkRunningStat();

        initialDelay();

        try {
            while (getSpiderStatus() != SPIDER_STATUS_STOPPED) {

                //暂停抓取
                if(pause) {
                    try {
                        this.pauseCountDown.await();
                    } catch (InterruptedException e) {
                        log.error("can't pause : ", e);
                    }

                    initialDelay();
                }

                // 从消息队列中取出request
                final Request request = queue.poll(name);

                if (request != null) {

                    if (request.getSleepTime()>0) {

                        try {
                            Thread.sleep(request.getSleepTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // 如果autoProxy打开并且request.getProxy()==null时，则从ProxyPool中取Proxy
                    if (autoProxy && request.getProxy() == null) {

                        Proxy proxy = ProxyPool.getProxy();

                        if (proxy!=null && Utils.checkProxy(proxy)) {
                            request.proxy(proxy);
                        }
                    }

                    // request请求之前的处理
                    if (request.getBeforeRequest()!=null) {

                        request.getBeforeRequest().process(request);
                    }

                    // request正在处理
                    downloader.download(request)
                            .map(new Function<Response, Page>() {

                                @Override
                                public Page apply(Response response) throws Exception {

                                    Page page = new Page();
                                    page.setRequest(request);
                                    page.setUrl(request.getUrl());
                                    page.setStatusCode(response.getStatusCode());

                                    if (Utils.isTextType(response.getContentType())) { // text/html

                                        page.setHtml(new Html(response.getContent()));

                                        return page;
                                    } else if (Utils.isApplicationJSONType(response.getContentType())) { // application/json

                                        // 将json字符串转化成Json对象，放入Page的"RESPONSE_JSON"字段。之所以转换成Json对象，是因为Json提供了toObject()，可以转换成具体的class。
                                        page.putField(Constant.RESPONSE_JSON,new Json(new String(response.getContent())));

                                        return page;
                                    } else if (Utils.isApplicationJSONPType(response.getContentType())) { // application/javascript

                                        // 转换成字符串，放入Page的"RESPONSE_JSONP"字段。
                                        // 由于是jsonp，需要开发者在Pipeline中自行去掉字符串前后的内容，这样就可以变成json字符串了。
                                        page.putField(Constant.RESPONSE_JSONP,new String(response.getContent()));

                                        return page;
                                    } else {

                                        page.putField(Constant.RESPONSE_RAW,response.getIs()); // 默认情况，保存InputStream

                                        return page;
                                    }
                                }
                            })
                            .map(new Function<Page, Page>() {

                                @Override
                                public Page apply(Page page) throws Exception {

                                    if (parser != null) {

                                        parser.process(page);
                                    }

                                    return page;
                                }
                            })
                            .map(new Function<Page, Page>() {

                                @Override
                                public Page apply(Page page) throws Exception {

                                    if (Preconditions.isNotBlank(pipelines)) {

                                        pipelines.stream()
                                                .forEach(pipeline -> pipeline.process(page.getResultItems()));
                                    }

                                    return page;
                                }
                            })
                            .retryWhen(new RetryWithDelay(maxRetries,retryDelayMillis,request.getUrl()))
                            .observeOn(Schedulers.io())
                            .subscribe(new Consumer<Page>() {

                                @Override
                                public void accept(Page page) throws Exception {

                                    log.info(page.getUrl());

                                    if (request.getAfterRequest()!=null) {

                                        request.getAfterRequest().process(page);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                    log.error(throwable.getMessage());
                                }
                            });
                } else {

                    break;
                }
            }
        } finally {

            stopSpider(downloader); // 爬虫停止
        }

    }

    private void checkIfRunning() {

        if (getSpiderStatus() == SPIDER_STATUS_RUNNING) {
            throw new SpiderException(String.format("Spider %s is already running!",name));
        }
    }

    private void checkRunningStat() {

        while (true) {

            int statNow = getSpiderStatus();
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw new SpiderException(String.format("Spider %s is already running!",name));
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

        if (downloader!=null) {
            IOUtils.closeQuietly(downloader);
        }

        stop();
    }

    public void stop() {

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            log.info(String.format("Spider %s stop success!",name));
        }
    }

    public void forceStop() {

        compositeDisposable.clear();

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            log.info(String.format("Spider %s force stop success!",name));
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

        if (stat.get()==SPIDER_STATUS_PAUSE) {
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
