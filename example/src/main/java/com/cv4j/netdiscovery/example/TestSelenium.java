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

        WebDriver driver = Browser.Chrome.init("example/chromedriver"); // 目前使用mac版本的chromedriver

        Spider.create()
                .name("tony1")
                .url("https://www.jianshu.com/u/4f2c483c12d8")
                .downloader(new SeleniumDownloader(driver))
                .run();
    }
}
