package cn.netdiscovery.kotlin.dsl

import cn.netdiscovery.core.Spider
import cn.netdiscovery.core.downloader.Downloader
import cn.netdiscovery.core.parser.Parser
import cn.netdiscovery.core.pipeline.Pipeline
import cn.netdiscovery.core.queue.Queue

/**
 * Created by tony on 2018/5/27.
 */
class SpiderWrapper {

    var name: String? = null

    var parser: Parser? = null

    var queue: Queue? = null

    var downloader: Downloader? = null

    var pipelines:List<Pipeline>? = null

    var urls:List<String>? = null

}

fun spider(init: SpiderWrapper.() -> Unit):Spider {

    val wrap = SpiderWrapper()

    wrap.init()

    return configSpider(wrap)
}

internal fun configSpider(wrap:SpiderWrapper):Spider {

    val spider = Spider.create(wrap.queue).name(wrap.name)

    wrap.urls?.let {

        spider.url(it)
    }

    spider.downloader(wrap.downloader)
            .parser(wrap.parser)

    wrap.pipelines?.let {

        it.forEach { // 这里的it指wrap?.pipelines

            spider.pipeline(it) // 这里的it指pipelines里的各个pipeline
        }
    }

    return spider
}