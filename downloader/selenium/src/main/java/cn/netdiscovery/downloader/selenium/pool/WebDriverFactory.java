package cn.netdiscovery.downloader.selenium.pool;

import cn.netdiscovery.downloader.selenium.Browser;
import com.cv4j.proxy.domain.Proxy;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/9.
 */
public class WebDriverFactory {

    public static WebDriver getWebDriver(String path, Browser browser, Proxy proxy) {

        WebDriver webDriver = null;
        switch (browser) {

            case CHROME:
                webDriver =  Browser.CHROME.init(path, proxy);
                break;

            case FIREFOX:
                webDriver = Browser.FIREFOX.init(path, proxy);
                break;

            case IE:
                webDriver = Browser.IE.init(path, proxy);
                break;

            case PHANTOMJS:
                webDriver = Browser.PHANTOMJS.init(path, proxy);
                break;

            default:
                break;
        }

        return webDriver;
    }
}
