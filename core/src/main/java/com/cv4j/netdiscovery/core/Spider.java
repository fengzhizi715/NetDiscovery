package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.cv4j.netdiscovery.core.downloader.vertx.VertxDownloader;
import com.cv4j.netdiscovery.core.parser.Parser;
import com.cv4j.netdiscovery.core.parser.selector.Html;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
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
        this.name = name;
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

    public Spider initialDelay(long initialDelay) {

        checkIfRunning();
        this.initialDelay = initialDelay;
        return this;
    }

    public Spider downloader(Downloader downloader) {

        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    public Spider parser(Parser parser) {

        checkIfRunning();
        this.parser = parser;
        return this;
    }

    public Spider pipeline(Pipeline pipeline) {

        checkIfRunning();
        this.pipelines.add(pipeline);
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

        checkIfRunning();
        this.autoProxy = true;
        return this;
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

                final Request request = queue.poll(name);

                if (request != null) {

                    if (request.getSleepTime()>0) {

                        try {
                            Thread.sleep(request.getSleepTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (autoProxy) {

                        Proxy proxy = ProxyPool.getProxy();

                        if (proxy!=null && request.getProxy() == null && Utils.checkProxy(proxy)) {
                            request.proxy(proxy);
                        }
                    }

                    if (request.getBeforeRequest()!=null) {

                        request.getBeforeRequest().process(request);
                    }

                    downloader.download(request)
                            .observeOn(Schedulers.io())
                            .map(new Function<Response, Page>() {

                                @Override
                                public Page apply(Response response) throws Exception {

                                    if (Utils.isTextType(response.getContentType())) { // text/html

                                        Page page = new Page();
                                        page.setHtml(new Html(response.getContent()));
                                        page.setRequest(request);
                                        page.setUrl(request.getUrl());
                                        page.setStatusCode(response.getStatusCode());

                                        return page;
                                    } else if (Utils.isApplicationJSONType(response.getContentType())) { // application/json

                                        Page page = new Page();
                                        page.putField(Constant.RESPONSE_JSON,new String(response.getContent())); // 将json字符串放入Page对象的"RESPONSE_JSON"字段
                                        page.setRequest(request);
                                        page.setUrl(request.getUrl());
                                        page.setStatusCode(response.getStatusCode());

                                        return page;
                                    } else {

                                        Page page = new Page();
//                                        page.putField("RAW_RESPONSE",response.getContent());
                                        page.setRequest(request);
                                        page.setUrl(request.getUrl());
                                        page.setStatusCode(response.getStatusCode());

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

//                                    if (page.getHtml()!=null) {

                                        if (Preconditions.isNotBlank(pipelines)) {

                                            pipelines.stream()
                                                    .forEach(pipeline -> pipeline.process(page.getResultItems()));
                                        }
//                                    }

                                    return page;
                                }
                            })
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
            throw new IllegalStateException(String.format("Spider %s is already running!",name));
        }
    }

    private void checkRunningStat() {

        while (true) {

            int statNow = getSpiderStatus();
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw new IllegalStateException(String.format("Spider %s is already running!",name));
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
