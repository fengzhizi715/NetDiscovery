package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.pipeline.ConsolePipeline;
import com.cv4j.netdiscovery.core.utils.UserAgent;
import com.cv4j.netdiscovery.extra.downloader.httpclient.HttpClientDownloader;

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
                .run();
    }
}
