package cn.netdiscovery.kotlin.coroutines

import cn.netdiscovery.core.domain.Request
import cn.netdiscovery.core.queue.Queue

/**
 * Created by tony on 2019-10-07.
 */

fun Queue.pushToRunninSpider(url: String, spider: Spider) {

    pushToRunninSpider(Request(url, spider.name), spider)
}

fun Queue.pushToRunninSpider(request: Request, spider: Spider) {

    push(request)
    spider.signalNewRequest()
}