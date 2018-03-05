package com.cv4j.netdiscovery.selenium.action;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by tony on 2018/3/6.
 */
public class ClickOn extends SeleniumAction {

    private final By by;

    public ClickOn(By by) {

        this.by = by;
    }

    @Override
    public SeleniumAction perform(WebDriver driver) {

        List<WebElement> elements = driver.findElements(by);
        if (elements != null) {
            for (WebElement currentElement : elements) {
                if (currentElement.isEnabled() &&
                        currentElement.isDisplayed() &&
                        currentElement.getSize().getHeight() > 0 &&
                        currentElement.getSize().getWidth() > 0) {

                    currentElement.click();

                    return this;
                }
            }
        }
        return this;
    }
}
