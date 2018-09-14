package com.cv4j.netdiscovery.example.jd

import com.cv4j.netdiscovery.core.Spider
import com.cv4j.netdiscovery.dsl.seleniumDownloader
import com.cv4j.netdiscovery.selenium.Browser

/**
 * Created by tony on 2018/9/14.
 */
object JDSpider4DSL {

    @JvmStatic
    fun main(args: Array<String>) {

        val seleniumDownloader = seleniumDownloader {

            path = "example/chromedriver"
            browser = Browser.CHROME

            addAction {
                action = BrowserAction()
            }

            addAction {
                action = SearchAction()
            }

            addAction {
                action = SortAction()
            }
        }

        val url = "https://search.jd.com/"

        Spider.create()
                .name("jd")
                .url(url)
                .downloader(seleniumDownloader)
                .parser(PriceParser())
                .pipeline(PricePipeline())
                .run()
    }
}