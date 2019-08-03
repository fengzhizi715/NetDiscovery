package com.cv4j.netdiscovery.example

import cn.netdiscovery.kotlin.dsl.spiderEngine


/**
 * Created by tony on 2018/6/12.
 */
fun main() {

    val spiderEngine = spiderEngine {

        port = 7070

        addSpider {

            name = "tony1"
        }

        addSpider {

            name = "tony2"
            urls = listOf("https://www.baidu.com")
        }
    }

    val spider = spiderEngine.getSpider("tony1")

    spider.repeatRequest(10000,"https://github.com/fengzhizi715").initialDelay(10000)

    spiderEngine.run()
}