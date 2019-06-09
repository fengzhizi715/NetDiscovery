package cn.netdiscovery.example.zookeeper;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;
import cn.netdiscovery.core.registry.EtcdRegistry;
import cn.netdiscovery.core.registry.ZKRegistry;

/**
 * Created by tony on 2019-06-09.
 */
public class Test {

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        spiderEngine
                .addSpider(Spider.create().name("tony1").url("http://www.163.com"))
                .addSpider(Spider.create().name("tony2").url("http://www.126.com"))
                .addSpider(Spider.create().name("tony3").url("https://www.baidu.com"))
                .setRegistry(new EtcdRegistry())
                .httpd()
                .run();
    }
}
