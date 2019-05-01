package cn.netdiscovery.downloader.selenium.action;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/3.
 */
public abstract class SeleniumAction {

    public abstract SeleniumAction perform(WebDriver driver);

    public SeleniumAction doIt(WebDriver driver) {

        return perform(driver);
    }

    public static SeleniumAction clickOn(By by) {
        return new ClickOn(by);
    }

    public static SeleniumAction getUrl(String url) {
        return new GetURL(url);
    }

    public static SeleniumAction goBack() {
        return new GoBack();
    }

    public static SeleniumAction closeTabs() {
        return new CloseTab();
    }
}
