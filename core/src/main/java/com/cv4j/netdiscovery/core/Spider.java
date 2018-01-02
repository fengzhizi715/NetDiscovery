package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.http.VertxClient;
import com.cv4j.netdiscovery.core.parser.Parser;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.cv4j.netdiscovery.core.queue.RedisQueue;
import com.safframework.tony.common.utils.Preconditions;
import com.safframework.tony.common.utils.StringUtils;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2017/12/22.
 */
@Slf4j
public class Spider {

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_STOPPED = 2;

    protected AtomicInteger stat = new AtomicInteger(SPIDER_STATUS_INIT);

    @Getter
    private String name = "spider";// 爬虫的名字

    private Parser parser;

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    private Queue queue;

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

    public Spider url(String url) {

        checkIfRunning();
        queue.push(new Request(url,name));
        return this;
    }

    public Spider request(Request request) {

        checkIfRunning();
        request.spiderName(name);
        queue.push(request);
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

    public void run() {

        VertxClient client = null;

        stat.compareAndSet(stat.get(),SPIDER_STATUS_RUNNING);

        while (true) {

            final Request request = queue.poll(name);

            if (request!=null) {

                client = new VertxClient(request);
                client.get()
                        .observeOn(Schedulers.io())
                        .map(new Function<HttpResponse<String>, Page>() {

                            @Override
                            public Page apply(HttpResponse<String> stringHttpResponse) throws Exception {

                                String html = stringHttpResponse.body();

                                Page page = new Page();
                                page.setHtml(html);
                                page.setRequest(request);
                                page.setUrl(request.getUrl());
                                page.setStatusCode(stringHttpResponse.statusCode());

                                return page;
                            }
                        })
                        .map(new Function<Page, Page>() {

                            @Override
                            public Page apply(Page page) throws Exception {

                                if (parser!=null) {

                                    parser.process(page);
                                }

                                return page;
                            }
                        })
                        .map(new Function<Page, Page>() {

                            @Override
                            public Page apply(Page page) throws Exception {

                                if (Preconditions.isNotBlank(pipelines)){

                                    for (Pipeline pipeline:pipelines) {

                                        pipeline.process(page.getResultItems());
                                    }
                                }

                                return page;
                            }
                        })
                        .subscribe(new Consumer<Page>() {

                            @Override
                            public void accept(Page page) throws Exception {

                                log.info(StringUtils.printObject(page));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                log.error(throwable.getMessage());
                            }
                        });
            } else {

                stat.set(SPIDER_STATUS_STOPPED);
                break;
            }
        }
    }

    protected void checkIfRunning() {

        if (stat.get() == SPIDER_STATUS_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    public static void main(String[] args) {

//        JedisPool pool = new JedisPool("127.0.0.1",6379);

        Spider.create()
                .request(new Request("http://www.163.com/"))
                .request(new Request("https://www.baidu.com/"))
                .run();
    }
}
