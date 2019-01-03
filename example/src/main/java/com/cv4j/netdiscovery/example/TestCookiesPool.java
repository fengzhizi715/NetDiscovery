package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;
import com.cv4j.netdiscovery.core.cookies.CookiesPool;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.downloader.urlconnection.UrlConnectionDownloader;

/**
 * Created by tony on 2019-01-02.
 */
public class TestCookiesPool {

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        Request request = new Request("https://www.facebook.com").saveCookie(true);

        Spider spider = Spider.create().name("tony").downloader(new UrlConnectionDownloader()).request(request);

        spiderEngine.addSpider(spider).run();

        CookiesPool.getInsatance()
                .getCookieGroup(request.getUrlParser().getHost())
                .getCookies()
                .forEach(httpCookie -> {
                    System.out.println(httpCookie.getDomain());
                    System.out.println(httpCookie.getName());
                    System.out.println(httpCookie.getMaxAge());
                    System.out.println(httpCookie.getPath());
                    System.out.println(httpCookie.getSecure());
                    System.out.println(httpCookie.getVersion());
                    System.out.println(httpCookie.getValue());
                    System.out.println("-------------------------");
                });
    }
}
