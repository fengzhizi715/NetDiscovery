package com.cv4j.netdiscovery.example

import cn.netdiscovery.core.pipeline.ConsolePipeline
import cn.netdiscovery.kotlin.dsl.spider


/**
 * Created by tony on 2018/5/27.
 */
object TestDSL4Spider {

    @JvmStatic
    fun main(args: Array<String>) {

        spider {

            name = "tony"

            urls = listOf("http://www.163.com/","https://www.baidu.com/")

            pipelines = listOf(ConsolePipeline())
        }.run()
    }

}