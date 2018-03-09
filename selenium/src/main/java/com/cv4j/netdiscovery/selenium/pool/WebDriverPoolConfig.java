package com.cv4j.netdiscovery.selenium.pool;

import com.cv4j.netdiscovery.selenium.Browser;
import lombok.Getter;

/**
 * Created by tony on 2018/3/9.
 */

public class WebDriverPoolConfig {

    @Getter
    private String path;

    @Getter
    private Browser browser;

    public WebDriverPoolConfig(String path, Browser browser) {

        this.path = path;
        this.browser = browser;
    }
}
