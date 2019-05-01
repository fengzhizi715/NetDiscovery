package cn.netdiscovery.example.jd;

import cn.netdiscovery.downloader.selenium.Utils;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 按照销量进行排序
 * Created by tony on 2018/6/12.
 */
public class SortAction extends SeleniumAction{

    @Override
    public SeleniumAction perform(WebDriver driver) {

        try {
            String saleSortBtn = "//*[@id=\"J_filter\"]/div[1]/div[1]/a[2]";
            Utils.clickElement(driver, By.xpath(saleSortBtn));
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
