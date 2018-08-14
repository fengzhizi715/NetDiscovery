package com.cv4j.netdiscovery.example

import com.cv4j.netdiscovery.coroutines.Spider
import com.cv4j.netdiscovery.coroutines.SpiderEngine

/**
 * Created by tony on 2018/8/14.
 */
fun main(args: Array<String>) {

    val spiderEngine = SpiderEngine.create()

    spiderEngine
            .addSpider(Spider.create().name("tony1").url("http://www.163.com"))
            .addSpider(Spider.create().name("tony2").url("http://www.126.com"))
            .addSpider(Spider.create().name("tony3").url("https://www.baidu.com"))
            .run()
}