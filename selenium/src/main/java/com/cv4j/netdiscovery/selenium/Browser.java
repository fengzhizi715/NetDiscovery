package com.cv4j.netdiscovery.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * Created by tony on 2018/1/28.
 */
public enum Browser implements WebDriverInitializer {

    Chrome {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.chrome.driver", path);
            return new ChromeDriver();
        }
    },
    Firefox {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.firefox.bin", path);
            return new FirefoxDriver();
        }
    },
    IE {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.ie.driver", path);
            return new InternetExplorerDriver();
        }
    }
}
