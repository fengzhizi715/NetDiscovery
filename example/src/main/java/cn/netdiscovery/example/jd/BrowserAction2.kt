package com.cv4j.netdiscovery.example.jd

import cn.netdiscovery.downloader.selenium.action.SeleniumAction
import cn.netdiscovery.kotlin.dsl.elementByXpath
import org.openqa.selenium.WebDriver

/**
 * Created by tony on 2018/9/23.
 */
class BrowserAction2 : SeleniumAction() {

    override fun perform(driver: WebDriver): SeleniumAction? {

        try {
            val searchText = "RxJava 2.x 实战"
            val searchInput = "//*[@id=\"keyword\"]"
            driver.elementByXpath(searchInput){

                this.sendKeys(searchText)
            }

            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return null
    }
}
