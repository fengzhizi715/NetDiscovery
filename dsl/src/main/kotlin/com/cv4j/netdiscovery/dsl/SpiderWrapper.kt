package com.cv4j.netdiscovery.dsl

import com.cv4j.netdiscovery.core.Spider
import com.cv4j.netdiscovery.core.downloader.Downloader
import com.cv4j.netdiscovery.core.parser.Parser
import com.cv4j.netdiscovery.core.pipeline.Pipeline
import com.cv4j.netdiscovery.core.queue.Queue

/**
 * Created by tony on 2018/5/27.
 */
class SpiderWrapper {

    var name: String? = null

    var parser: Parser? = null

    var queue: Queue? = null

    var downloader: Downloader? = null

    var pipelines:Set<Pipeline>? = null

    var urls:List<String>? = null

}

fun spider(init: SpiderWrapper.() -> Unit):Spider {

    val wrap = SpiderWrapper()

    wrap.init()

    return config(wrap)
}

private fun config(wrap:SpiderWrapper):Spider {

    val spider = Spider.create(wrap?.queue)
            .name(wrap?.name)

    var urls = wrap?.urls

    urls?.let {

        spider.url(urls)
    }

    spider.downloader(wrap?.downloader)
            .parser(wrap?.parser)

    wrap?.pipelines?.let {

        it.forEach { // 这里的it指wrap?.pipelines

            spider.pipeline(it) // 这里的it指pipelines里的各个pipeline
        }
    }

    return spider
}