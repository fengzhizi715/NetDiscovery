package cn.netdiscovery.downloader.selenium.action;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by tony on 2018/3/6.
 */
public class GetURL extends SeleniumAction {

    private final String url;

    public GetURL(String url) {
        this.url = url;
    }
    @Override
    public SeleniumAction perform(WebDriver driver) {

        driver.get(url);

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (Exception e) {
        }

        return this;
    }
}
