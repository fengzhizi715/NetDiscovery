package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.extra.downloader.httpclient.HttpClientDownloader;

/**
 * Created by tony on 2018/1/22.
 */
public class Test {

    public static void main(String[] args) {

        SpiderEngine engine = SpiderEngine.create();

        Spider spider = Spider.create()
                .name("tony")
                .repeatRequest(10000,"http://www.163.com")
                .initialDelay(10000);

        engine.addSpider(spider);
        engine.httpd(8080);
        engine.run();
    }
}
