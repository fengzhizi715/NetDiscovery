package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;

/**
 * Created by tony on 2018/1/22.
 */
public class Test {

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        spiderEngine
                .addSpider(Spider.create().name("tony1").url("http://www.163.com"))
                .addSpider(Spider.create().name("tony2").url("http://www.126.com"))
                .addSpider(Spider.create().name("tony3").url("https://www.baidu.com"))
                .httpd()
                .run();
    }
}
