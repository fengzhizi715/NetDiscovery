package com.cv4j.netdiscovery.selenium.pool;

import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.proxy.domain.Proxy;
import lombok.Getter;

import java.util.List;

/**
 * Created by tony on 2018/3/9.
 */

public class WebDriverPoolConfig {

    @Getter
    private String path;

    @Getter
    private Browser browser;

    @Getter
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
}
