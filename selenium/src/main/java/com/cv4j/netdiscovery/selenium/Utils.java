package com.cv4j.netdiscovery.selenium;

import com.safframework.tony.common.utils.FileUtils;
import com.safframework.tony.common.utils.IOUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by tony on 2018/3/4.
 */
public class Utils {

    /**
     * 滚动窗口。
     * @param driver
     * @param height
     */
    public static void scroll(WebDriver driver, int height){
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+height+" );");
    }

    /**
     * 重新调整窗口大小，以适应页面，需要耗费一定时间。建议等待合理的时间。
     * @param driver
     */
    public static void loadAll(WebDriver driver){
        Dimension od= driver.manage().window().getSize();
        int width=driver.manage().window().getSize().width;
        //尝试性解决：https://github.com/ariya/phantomjs/issues/11526问题
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        long height=(Long)((JavascriptExecutor)driver).executeScript("return document.body.scrollHeight;");
        driver.manage().window().setSize(new Dimension(width, (int)height));
        driver.navigate().refresh();
    }

    public static void refresh(WebDriver driver){
        driver.navigate().refresh();
    }

    public static void taskScreenShot(WebDriver driver,String pathName){

        //指定了OutputType.FILE做为参数传递给getScreenshotAs()方法，其含义是将截取的屏幕以文件形式返回。
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        //利用IOUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象。

        try {
            IOUtils.copyFile(srcFile, new File(pathName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 操作关闭模态窗口
     * @param driver
     * @param type 如Id,ClassName
     * @param sel 选择器
     */
    public static void clickModal(WebDriver driver,String type,String sel){
        String js="document.getElementsBy"+type+"('"+sel+"')[0].click();";
        ((JavascriptExecutor)driver).executeScript(js);
    }

    /**
     * 点击一个元素
     * @param driver
     * @param by
     */
    public static void clickElement(WebDriver driver,By by){
        WebElement tmp=driver.findElement(by);
        Actions actions=new Actions(driver);
        actions.moveToElement(tmp).click().perform();
    }

    public static void clickElement(WebDriver driver,WebElement tmp){
        Actions actions=new Actions(driver);
        actions.moveToElement(tmp).click().perform();
    }

    public static Object execJs(WebDriver driver,String js){
        return ((JavascriptExecutor)driver).executeScript(js);
    }
    
    public static void clickByJsCssSelector(WebDriver driver,String cssSelector){
        String js="document.querySelector('"+cssSelector+"').click();";
        ((JavascriptExecutor)driver).executeScript(js);
    }
}
