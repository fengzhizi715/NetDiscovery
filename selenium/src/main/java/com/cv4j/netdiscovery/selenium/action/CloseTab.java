package com.cv4j.netdiscovery.selenium.action;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2018/3/6.
 */
public class CloseTab extends SeleniumAction{

    @Override
    public SeleniumAction perform(WebDriver driver) {

        String originalWindowHandle = driver.getWindowHandle();
        List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());

        if (windowHandles.size() == 1) {
            System.out.println("Skipping this step... as it possible that no new tab/window was opened");
            goBack().doIt(driver);
            return this;
        }

        windowHandles.remove(originalWindowHandle);
        for (String currentWindow : windowHandles) {
            driver.switchTo().window(currentWindow);
            driver.close();
        }

        driver.switchTo().window(originalWindowHandle);
        return this;
    }
}
