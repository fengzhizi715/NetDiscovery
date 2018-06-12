package com.cv4j.netdiscovery.example.jd;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.netdiscovery.selenium.action.SeleniumAction;
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPool;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2018/6/12.
 */
public class JDSpider {

    public static void main(String[] args) {
        WebDriverPoolConfig config = new WebDriverPoolConfig("example/chromedriver", Browser.CHROME);
        WebDriverPool.init(config);

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
