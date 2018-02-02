package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;

/**
 * Created by tony on 2018/2/1.
 */
public class TestRepeatRequest {

    public static void main(String[] args) {

        SpiderEngine engine = SpiderEngine.create();

        Spider spider = Spider.create()
                .name("tony")
                .repeatRequest(10000,"http://www.163.com")
                .repeatRequest(12000,"http://www.baidu.com")
                .repeatRequest(15000,"http://www.126.com")
                .initialDelay(15000);

        engine.addSpider(spider).httpd(8080).run();
    }
}
