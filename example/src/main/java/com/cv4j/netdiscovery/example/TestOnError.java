package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.downloader.vertx.VertxDownloader;

/**
 * @author bdq
 * @date 2018/9/17
 */
public class TestOnError {

    public static void main(String[] args) {
        Spider spider = Spider.create()
                .downloader(new VertxDownloader());
        Request request = new Request("https://www.google.com");
        request.onErrorRequest(new Request.OnErrorRequest() {
            @Override
            public void process(Request request) {
                System.out.println("process on error request!");
            }
        });
        spider.request(request);
        spider.run();
    }
}
