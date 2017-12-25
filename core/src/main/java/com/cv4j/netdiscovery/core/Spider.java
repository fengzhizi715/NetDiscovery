package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.http.VertxClient;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.safframework.tony.common.utils.StringUtils;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by tony on 2017/12/22.
 */
public class Spider {

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_STOPPED = 2;

    @Getter
    @Setter
    private String name;// 爬虫的名字

    private Request request;

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    public static Spider create() {

        return new Spider();
    }

    public Spider name(String name) {

        this.name = name;
        return this;
    }

    public Spider pipeline(Pipeline pipeline) {

        this.pipelines.add(pipeline);
        return this;
    }

    public Spider clearPipeline() {

        this.pipelines.clear();
        return this;
    }

    public Spider request(Request request) {

        this.request = request;
        return this;
    }

    public void run() {

        if (request!=null) {

            VertxClient client = new VertxClient(request);
            client.get()
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
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Page>() {

                        @Override
                        public void accept(Page page) throws Exception {

                            System.out.println(page.getHtml());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            System.out.println(throwable.getMessage());
                        }
                    });
        }
    }

    public static void main(String[] args) {

        Spider spider = Spider.create();
        spider.request(new Request("http://www.163.com/"));
        spider.run();
    }
}
