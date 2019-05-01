package cn.netdiscovery.example;

import cn.netdiscovery.kotlin.coroutines.Spider;
import cn.netdiscovery.kotlin.coroutines.SpiderEngine;

/**
 * Created by tony on 2018/8/14.
 */
public class TestSpider4Coroutines {

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        spiderEngine
                .addSpider(Spider.create().name("tony1").url("http://www.163.com"))
                .addSpider(Spider.create().name("tony2").url("http://www.126.com"))
                .addSpider(Spider.create().name("tony3").url("https://www.baidu.com"))
                .httpd(8080)
                .run();
    }
}
