package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by tony on 2018/3/3.
 */
public class TestSelenium {

    static {
        System.setProperty("webdriver.chrome.driver", "example/chromedriver");
    }

    public static void main(String[] args) {

        WebDriver driver = new ChromeDriver();

        Spider.create()
                .name("tony1")
                .url("http://www.163.com")
                .downloader(new SeleniumDownloader(driver))
                .run();
    }
}
