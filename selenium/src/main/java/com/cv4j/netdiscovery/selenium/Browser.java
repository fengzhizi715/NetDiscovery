package com.cv4j.netdiscovery.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by tony on 2018/1/28.
 */
public enum Browser implements WebDriverInitializer {

    CHROME {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.chrome.driver", path);
            return new ChromeDriver();
        }
    },
    FIREFOX {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.gecko.driver", path);
            return new FirefoxDriver();
        }
    },
    IE {
        @Override
        public WebDriver init(String path) {
            System.setProperty("webdriver.ie.driver", path);
            return new InternetExplorerDriver();
        }
    },
    PHANTOMJS {
        @Override
        public WebDriver init(String path) {

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.binary.path", path);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability("takesScreenshot", true);
            capabilities.setCapability("cssSelectorsEnabled", true);
//            capabilities.setBrowserName("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
//            capabilities.setPlatform(Platform.LINUX);

            return new PhantomJSDriver(capabilities);
        }
    }
}
