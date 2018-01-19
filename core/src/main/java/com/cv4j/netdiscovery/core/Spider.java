package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.http.VertxClient;
import com.cv4j.netdiscovery.core.parser.Parser;
import com.cv4j.netdiscovery.core.parser.selector.Html;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.cv4j.netdiscovery.core.utils.Utils;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spider可以单独使用，每个Spider只能处理一种Parser，不同的Parser需要不同的Spider
 * <p>
 * Created by tony on 2017/12/22.
 */
@Slf4j
public class Spider {

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_STOPPED = 2;

    protected AtomicInteger stat = new AtomicInteger(SPIDER_STATUS_INIT);

    @Getter
    private String name = "spider";// 爬虫的名字，默认使用spider

    private Parser parser;

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    @Getter
    private Queue queue;

    private boolean autoProxy = false;

    private Spider() {
        queue = new DefaultQueue();
    }

    private Spider(Queue queue) {
        this.queue = queue;
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

        VertxClient client = null;

        try {
            while (true && getSpiderStatus() == SPIDER_STATUS_RUNNING) {

                final Request request = queue.poll(name);

                if (request != null) {

                    if (autoProxy) {

                        Proxy proxy = ProxyPool.getProxy();

                        if (proxy!=null && request.getProxy() == null && Utils.checkProxy(proxy)) {
                            request.proxy(proxy);
                        }
                    }

                    client = new VertxClient(request);
                    client.request()
                            .observeOn(Schedulers.io())
                            .map(new Function<HttpResponse<String>, Page>() {

                                @Override
                                public Page apply(HttpResponse<String> stringHttpResponse) throws Exception {

                                    String html = stringHttpResponse.body();

                                    Page page = new Page();
                                    page.setHtml(new Html(html));
                                    page.setRequest(request);
                                    page.setUrl(request.getUrl());
                                    page.setStatusCode(stringHttpResponse.statusCode());

                                    return page;
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
                            .subscribe(new Consumer<Page>() {

                                @Override
                                public void accept(Page page) throws Exception {

                                    log.info(page.getUrl());
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

            stopSpider(client); // 爬虫停止
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

    private void stopSpider(VertxClient client) {

        if (client!=null) {
            client.close(); // 关闭VertxClient
        }

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            log.info(String.format("Spider %s stop success!",name));
        } else {
            log.info(String.format("Spider %s stop fail!",name));
        }
    }
}
