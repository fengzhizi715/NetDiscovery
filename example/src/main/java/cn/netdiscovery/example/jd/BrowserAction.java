package cn.netdiscovery.example.jd;

import cn.netdiscovery.downloader.selenium.utils.SeleniumUtils;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by tony on 2018/6/12.
 */
public class BrowserAction extends SeleniumAction{

    @Override
    public SeleniumAction perform(WebDriver driver) {

        try {
            String searchText = "RxJava 2.x 实战";
            String searchInput = "//*[@id=\"keyword\"]";
            WebElement userInput = SeleniumUtils.getWebElementByXpath(driver, searchInput);
            userInput.sendKeys(searchText);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
