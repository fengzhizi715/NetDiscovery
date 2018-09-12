package com.cv4j.netdiscovery.example

import com.cv4j.netdiscovery.dsl.spider
import com.cv4j.netdiscovery.dsl.spiderEngine

/**
 * Created by tony on 2018/6/12.
 */
object TestDSL4SpiderEngine {

    @JvmStatic
    fun main(args: Array<String>) {

        val spiderEngine = spiderEngine {

            port = 7070

            addSpider {

                name = "tony2"
                urls = listOf("https://www.baidu.com")
            }
        }

        val spider1 = spider {

            name = "tony1"
        }

        spider1.repeatRequest(10000,"https://github.com/fengzhizi715")
                .initialDelay(10000)

        spiderEngine.addSpider(spider1)

        spiderEngine.run()
    }

}