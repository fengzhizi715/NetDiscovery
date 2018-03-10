package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPool;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPoolConfig;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/3.
 */
public class TestSelenium {

    public static void main(String[] args) {

        WebDriverPoolConfig config = new WebDriverPoolConfig("example/chromedriver",Browser.Chrome); //设置浏览器的驱动程序和浏览器到类型

        WebDriverPool.init(config); // 需要先使用init，才能使用WebDriverPool

        //创建SeleniumAction类并设置到Downloader类
        TestSeleniumAction testSeleniumAction = new TestSeleniumAction();
        SeleniumDownloader seleniumDownloader = new SeleniumDownloader(testSeleniumAction);

        //设置并启动爬虫
        Spider.create()
                .name("testseleinum")
                .url("https://www.jianshu.com/u/4f2c483c12d8")
                .downloader(seleniumDownloader)
                .run();
    }
}
