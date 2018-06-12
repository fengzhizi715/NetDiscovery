package com.cv4j.netdiscovery.selenium.pool;

import com.cv4j.netdiscovery.selenium.Browser;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/9.
 */
public class WebDriverFactory {

    public static WebDriver getWebDriver(String path,Browser browser) {

        switch (browser) {

            case CHROME:

                return Browser.CHROME.init(path);

            case FIREFOX:

                return Browser.FIREFOX.init(path);

            case IE:

                return Browser.IE.init(path);

            case PHANTOMJS:

                return Browser.PHANTOMJS.init(path);
        }

        return null;

    }
}
