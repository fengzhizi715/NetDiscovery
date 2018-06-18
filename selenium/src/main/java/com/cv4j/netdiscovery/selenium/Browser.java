package com.cv4j.netdiscovery.selenium;

import com.cv4j.proxy.domain.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;

/**
 * Created by tony on 2018/1/28.
 */
public enum Browser implements WebDriverInitializer {

    CHROME {
        @Override
        public WebDriver init(String path, Proxy proxy) {
            System.setProperty("webdriver.chrome.driver", path);

            if (proxy!=null) {

                ChromeOptions options = new ChromeOptions();

                // Add the WebDriver proxy capability.
                options.setCapability("proxy", ProxyUtils.toSeleniumProxy(proxy));

                return new ChromeDriver(options);
            } else {
                return new ChromeDriver();
            }
        }
    },
    FIREFOX {
        @Override
        public WebDriver init(String path, Proxy proxy) {
            System.setProperty("webdriver.gecko.driver", path);
            return new FirefoxDriver();
        }
    },
    IE {
        @Override
        public WebDriver init(String path, Proxy proxy) {
            System.setProperty("webdriver.ie.driver", path);
            return new InternetExplorerDriver();
        }
    },
    PHANTOMJS {
        @Override
        public WebDriver init(String path, Proxy proxy) {

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.binary.path", path);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability("takesScreenshot", true);
            capabilities.setCapability("cssSelectorsEnabled", true);
            return new PhantomJSDriver(capabilities);
        }
    }
}
