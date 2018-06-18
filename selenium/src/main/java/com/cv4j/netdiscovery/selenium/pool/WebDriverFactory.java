package com.cv4j.netdiscovery.selenium.pool;

import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.proxy.domain.Proxy;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/9.
 */
public class WebDriverFactory {

    public static WebDriver getWebDriver(String path, Browser browser, Proxy proxy) {

        switch (browser) {

            case CHROME:

                return Browser.CHROME.init(path,proxy);

            case FIREFOX:

                return Browser.FIREFOX.init(path,proxy);

            case IE:

                return Browser.IE.init(path,proxy);

            case PHANTOMJS:

                return Browser.PHANTOMJS.init(path,proxy);
        }

        return null;

    }
}
