package cn.netdiscovery.kotlin.dsl

import cn.netdiscovery.core.Spider
import cn.netdiscovery.core.SpiderEngine
import cn.netdiscovery.core.queue.DefaultQueue
import cn.netdiscovery.core.queue.Queue
import com.cv4j.proxy.domain.Proxy

/**
 * Created by tony on 2018/5/28.
 */
class SpiderEngineWrapper {

    var queue: Queue? = null

    var port: Int = 8080

    var proxyList:List<Proxy>? = null

    private val spiders = mutableSetOf<Spider>()

    fun addSpider(block: SpiderWrapper.() -> Unit) {
        val spiderWrapper = SpiderWrapper()
        spiderWrapper.block()
        val spider = configSpider(spiderWrapper)
        spiders.add(spider)
    }

    internal fun getSpiders() = spiders
}

fun spiderEngine(init: SpiderEngineWrapper.() -> Unit): SpiderEngine {

    val wrap = SpiderEngineWrapper()

    wrap.init()

    return configSpiderEngine(wrap)
}

internal fun configSpiderEngine(wrap: SpiderEngineWrapper): SpiderEngine {

    val engine = SpiderEngine.create(wrap.queue?: DefaultQueue())

    engine.proxyList(wrap.proxyList)

    wrap.getSpiders().forEach {

        engine.addSpider(it)
    }

    engine.httpd(wrap.port)

    return engine
}