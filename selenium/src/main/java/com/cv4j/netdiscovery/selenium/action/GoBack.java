package com.cv4j.netdiscovery.selenium.action;

import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/6.
 */
public class GoBack extends SeleniumAction{

    @Override
    public SeleniumAction perform(WebDriver driver) {

        driver.navigate().back();
        return this;
    }
}
