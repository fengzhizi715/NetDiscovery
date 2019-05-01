package com.cv4j.netdiscovery.example.jd;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.downloader.selenium.Browser;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
import cn.netdiscovery.downloader.selenium.downloader.SeleniumDownloader;
import cn.netdiscovery.downloader.selenium.pool.WebDriverPool;
import cn.netdiscovery.downloader.selenium.pool.WebDriverPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2018/6/12.
 */
public class JDSpider {

    public static void main(String[] args) {

        WebDriverPoolConfig config = new WebDriverPoolConfig("example/chromedriver",Browser.CHROME); //设置浏览器的驱动程序和浏览器的类型，浏览器的驱动程序要跟操作系统匹配。
        WebDriverPool.init(config); // 需要先使用init，才能使用WebDriverPool

        List<SeleniumAction> actions = new ArrayList<>();
        actions.add(new BrowserAction());
        actions.add(new SearchAction());
        actions.add(new SortAction());

        SeleniumDownloader seleniumDownloader = new SeleniumDownloader(actions);

        String url = "https://search.jd.com/";

        Spider.create()
                .name("jd")
                .url(url)
                .downloader(seleniumDownloader)
                .parser(new PriceParser())
                .pipeline(new PricePipeline())
                .run();
    }
}
