package cn.netdiscovery.example;

import cn.netdiscovery.downloader.selenium.utils.SeleniumUtils;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
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
                SeleniumUtils.scrollBy(driver, 500);
                Thread.sleep(2000);
            }

            WebElement queryInput = SeleniumUtils.getWebElementByXpath(driver, "//*[@id='q']");
            queryInput.sendKeys("fengzhizi715");

            SeleniumUtils.clickElement(driver, By.xpath("//*[@id='menu']/ul/li[3]/form/a"));

            Thread.sleep(2000);

            //对当前网页截屏
            SeleniumUtils.taskScreenShot(driver, "test.png");
            
        } catch(InterruptedException e) {
        }

        return null;
    }
}