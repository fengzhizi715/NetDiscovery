package cn.netdiscovery.example.jd;

import cn.netdiscovery.downloader.selenium.utils.SeleniumUtils;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/6/12.
 */
public class SearchAction extends SeleniumAction {

    @Override
    public SeleniumAction perform(WebDriver driver) {

        try {
            String searchBtn = "/html/body/div[2]/form/input[4]";
            SeleniumUtils.clickElement(driver, By.xpath(searchBtn));
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
