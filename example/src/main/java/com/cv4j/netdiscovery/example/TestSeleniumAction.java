package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.selenium.Utils;
import com.cv4j.netdiscovery.selenium.action.SeleniumAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestSeleniumAction extends SeleniumAction {
    @Override
    public SeleniumAction perform(WebDriver driver) {
        try {
            //浏览器窗口最大化
//            driver.manage().window().maximize();
            //停顿2秒
            Thread.sleep(2000);

            //往下滚动500px，滚10次
            for (int i = 0; i < 10; i++) {
                Utils.scrollBy(driver, 500);
                Thread.sleep(2000);
            }

            WebElement queryInput = Utils.getWebElementByXpath(driver, "//*[@id='q']");
            queryInput.sendKeys("fengzhizi715");

            Utils.clickElement(driver, By.xpath("//*[@id='menu']/ul/li[3]/form/a"));

            Thread.sleep(2000);

            //对当前网页截屏
            Utils.taskScreenShot(driver, "test.png");
            
        } catch(InterruptedException e) {
        }

        return null;
    }
}