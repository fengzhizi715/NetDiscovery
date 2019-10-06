package cn.netdiscovery.example

import cn.netdiscovery.core.pipeline.ConsolePipeline
import cn.netdiscovery.kotlin.dsl.spider


/**
 * Created by tony on 2018/5/27.
 */
fun main() {

    val spider = spider {

        name = "tony"

        urls = listOf("http://www.163.com/","https://www.baidu.com/")

        pipelines = listOf(ConsolePipeline())
    }

    spider.run()
}