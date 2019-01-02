package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;
import com.cv4j.netdiscovery.core.cookies.CookieGroup;
import com.cv4j.netdiscovery.core.cookies.CookieManager;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.downloader.urlconnection.UrlConnectionDownloader;
import com.cv4j.netdiscovery.extra.downloader.httpclient.HttpClientDownloader;
import com.cv4j.netdiscovery.extra.downloader.okhttp.OkHttpDownloader;

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
