package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/3.
 */
public class TestSelenium {

    public static void main(String[] args) {

        //设置浏览器的驱动程序
//        WebDriver driver = Browser.Chrome.init("example/chromedriver");             //for mac
        WebDriver driver = Browser.Chrome.init("example/chromedriver.exe");//for windows（要和本地浏览器版本匹配）
        //创建SeleniumAction类并设置到Downloader类
        TestSeleniumAction testSeleniumAction = new TestSeleniumAction();
        SeleniumDownloader seleniumDownloader = new SeleniumDownloader(driver, testSeleniumAction);
        //设置并启动爬虫
        Spider.create()
                .name("testseleinum")
                .url("https://www.jianshu.com/u/4f2c483c12d8")
                .downloader(seleniumDownloader)
                .run();
    }
}
