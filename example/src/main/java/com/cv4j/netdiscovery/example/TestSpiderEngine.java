package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;

/**
 * Created by tony on 2018/1/30.
 */
public class TestSpiderEngine {

    public static void main(String[] args) {

        SpiderEngine engine = SpiderEngine.create();

        Spider spider = Spider.create()
                .name("tony1")
                .repeatRequest(10000,"http://www.163.com")
                .initialDelay(10000);

        engine.addSpider(spider);

        Spider spider2 = Spider.create()
                .name("tony2")
                .repeatRequest(10000,"https://www.baidu.com")
                .initialDelay(10000);

        engine.addSpider(spider2);

        Spider spider3 = Spider.create()
                .name("tony3")
                .repeatRequest(10000,"http://www.126.com")
                .initialDelay(10000);

        engine.addSpider(spider3);

        engine.addSpider(Spider.create().name("tony").url("https://www.jianshu.com/"));

        engine.httpd(8080);
        engine.run();
    }
}
