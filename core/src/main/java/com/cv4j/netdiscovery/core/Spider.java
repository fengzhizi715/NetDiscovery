package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.http.VertxClient;
import com.cv4j.netdiscovery.core.parser.Parser;
import com.cv4j.netdiscovery.core.parser.selector.Html;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.cv4j.netdiscovery.core.queue.RedisQueue;
import com.cv4j.netdiscovery.core.utils.Utils;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.Preconditions;
import com.safframework.tony.common.utils.StringUtils;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spider可以单独使用，每个Spider处理一种Parser，不同的Parser需要不同的Spider
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

    private Queue queue;

    private boolean useProxy = false;

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

        return new Spider(queue);
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

        this.pipelines.clear();
        return this;
    }

    public Spider useProxy(boolean useProxy) {

        this.useProxy = useProxy;
        return this;
    }

    public void run() {

        checkRunningStat();

        VertxClient client = null;

        try {
            while (true && getSpiderStatus() == SPIDER_STATUS_RUNNING) {

                final Request request = queue.poll(name);

                if (request != null) {

                    if (useProxy) {

                        Proxy proxy = ProxyPool.getProxy();

                        if (proxy!=null && Utils.checkProxy(proxy)) {
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

//                                log.info(StringUtils.printObject(page));
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
            throw new IllegalStateException("Spider is already running!");
        }
    }

    private void checkRunningStat() {
        while (true) {

            int statNow = getSpiderStatus();
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }

            if (stat.compareAndSet(statNow, SPIDER_STATUS_RUNNING)) {
                break;
            }
        }
    }

    public int getSpiderStatus() {

        return stat.get();
    }

    public void stopSpider(VertxClient client) {

        if (client!=null) {
            client.close(); // 关闭网络框架
        }

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) {
            log.info("Spider " + name + " stop success!");
        } else {
            log.info("Spider " + name + " stop fail!");
        }
    }

    public static void main(String[] args) {

        JedisPool pool = new JedisPool("127.0.0.1", 6379);

        Spider.create()
                .name("tony")
                .request(new Request("http://www.163.com/"))
                .request(new Request("https://www.baidu.com/"))
                .request(new Request("https://www.baidu.com/"))
                .run();
    }
}
