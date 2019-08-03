package cn.netdiscovery.kotlin.dsl

import cn.netdiscovery.core.domain.Request
import io.vertx.core.http.HttpMethod

/**
 * Created by tony on 2018/9/18.
 */
class RequestWrapper {

    private val headerContext = HeaderContext()
    private val extrasContext = ExtrasContext()

    var url: String? = null

    var userAgent:String? = null

    var spiderName: String? = null

    var httpMethod: HttpMethod = HttpMethod.GET

    var checkDuplicate = true

    var saveCookie = false

    var debug = false

    var sleepTime: Long = 0

    var downloadDelay: Long = 0

    var domainDelay: Long = 0

    fun header(init: HeaderContext.() -> Unit) {

        headerContext.init()
    }

    fun extras(init: ExtrasContext.() -> Unit) {

        extrasContext.init()
    }

    internal fun getHeaderContext() = headerContext

    internal fun getExtrasContext() = extrasContext
}

class HeaderContext {

    private val map: MutableMap<String, String> = mutableMapOf()

    infix fun String.to(v: String) {
        map[this] = v
    }

    internal fun forEach(action: (k: String, v: String) -> Unit) = map.forEach(action)
}

class ExtrasContext {

    private val map: MutableMap<String, Any> = mutableMapOf()

    infix fun String.to(v: Any) {
        map[this] = v
    }

    internal fun forEach(action: (k: String, v: Any) -> Unit) = map.forEach(action)
}

fun request(init: RequestWrapper.() -> Unit): Request {

    val wrap = RequestWrapper()

    wrap.init()

    return configRequest(wrap)
}

private fun configRequest(wrap: RequestWrapper): Request {

    val request =  Request(wrap.url)
            .ua(wrap.userAgent)
            .spiderName(wrap.spiderName)
            .httpMethod(wrap.httpMethod)
            .checkDuplicate(wrap.checkDuplicate)
            .saveCookie(wrap.saveCookie)
            .debug(wrap.debug)
            .sleep(wrap.sleepTime)
            .downloadDelay(wrap.downloadDelay)
            .domainDelay(wrap.domainDelay)

    wrap.getHeaderContext().forEach { k, v ->

        request.header(k,v)
    }

    wrap.getExtrasContext().forEach { k, v ->

        request.putExtra(k,v)
    }

    return request
}
