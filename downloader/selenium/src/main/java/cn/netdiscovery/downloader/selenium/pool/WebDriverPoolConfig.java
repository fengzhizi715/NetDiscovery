package cn.netdiscovery.downloader.selenium.pool;

import cn.netdiscovery.downloader.selenium.Browser;
import com.cv4j.proxy.domain.Proxy;

import java.util.List;

/**
 * Created by tony on 2018/3/9.
 */

public class WebDriverPoolConfig {

    private String path;

    private Browser browser;

    private List<Proxy> proxies;

    public WebDriverPoolConfig(String path) {

        this.path = path;

        if (path.contains("chrome")) {

            this.browser = Browser.CHROME;
        } else if (path.contains("firefox")){

            this.browser = Browser.FIREFOX;
        } else if (path.contains("ie")) {

            this.browser = Browser.IE;
        } else if (path.contains("phatomjs")){

            this.browser = Browser.PHANTOMJS;
        }
    }

    public WebDriverPoolConfig(String path, Browser browser) {

        this.path = path;
        this.browser = browser;
    }

    public WebDriverPoolConfig(String path, Browser browser,List<Proxy> proxies) {

        this.path = path;
        this.browser = browser;
        this.proxies = proxies;
    }

    public String getPath() {
        return path;
    }

    public Browser getBrowser() {
        return browser;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }
}
