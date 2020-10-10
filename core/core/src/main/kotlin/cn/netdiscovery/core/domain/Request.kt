package cn.netdiscovery.core.domain

import cn.netdiscovery.core.utils.URLParser
import cn.netdiscovery.core.utils.UserAgent
import com.cv4j.proxy.domain.Proxy
import com.safframework.tony.common.collection.NoEmptyHashMap
import com.safframework.tony.common.utils.Preconditions
import io.vertx.core.http.HttpMethod
import org.apache.commons.lang3.RandomUtils
import java.io.Serializable
import java.net.MalformedURLException

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.Request
 * @author: Tony Shen
 * @date: 2020-10-10 15:34
 * @version: V1.0 爬虫网络请求的封装
 */
class Request : Serializable {

    var url: String? = null
    var urlParser: URLParser? = null
    var userAgent: String? = null
    var proxy: Proxy? = null
    var spiderName: String? = null
    var httpMethod: HttpMethod? = null
    var charset: String? = null
    var checkDuplicate = true
    var saveCookie = false // request 是否需要保存cookie
    var debug = false // 在 debug 模式下请求 request 会将 response 缓存到 RxCahce。(如果 RxCache 配置了 Persistence，可以把 response 持久化)
    var sleepTime: Long = 0 // 每次请求url之前先sleep一段时间
    var downloadDelay: Long = 0
    var domainDelay: Long = 0
    val header: MutableMap<String, String?> = NoEmptyHashMap()
    var extras: MutableMap<String, Any>? = null // extras 中的数据可以在pipeline中使用

    var priority: Long = 0 // request的优先级，数字越大优先级越高
    var httpRequestBody: HttpRequestBody? = null
    var beforeRequest: BeforeRequest? = null
    var afterRequest: AfterRequest? = null
    var onErrorRequest: OnErrorRequest? = null

    constructor() {}
    constructor(url: String?) {
        this.url = url
        try {
            urlParser = URLParser(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        httpMethod = HttpMethod.GET
        autoUA()
    }

    constructor(url: String?, spiderName: String?) {
        this.url = url
        try {
            urlParser = URLParser(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        this.spiderName = spiderName
        httpMethod = HttpMethod.GET
        autoUA()
    }

    constructor(url: String?, spiderName: String?, httpMethod: HttpMethod?) {
        this.url = url
        try {
            urlParser = URLParser(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        this.spiderName = spiderName
        this.httpMethod = httpMethod
        autoUA()
    }

    fun ua(userAgent: String?): Request {
        this.userAgent = userAgent
        if (Preconditions.isNotBlank(userAgent)) {
            header["User-Agent"] = userAgent
        }
        return this
    }

    /**
     * 从 UserAgent 类中随机获取User-Agent
     */
    private fun autoUA() {
        userAgent = UserAgent.getUserAgent()
        if (Preconditions.isNotBlank(userAgent)) {
            header["User-Agent"] = userAgent
        }
    }

    /**
     * request使用的代理，其优先级高于Spider所设置的autoProxy
     *
     * @param proxy
     * @return
     */
    fun proxy(proxy: Proxy?): Request {
        this.proxy = proxy
        return this
    }

    /**
     * 爬虫的名字
     *
     * @param spiderName
     * @return
     */
    fun spiderName(spiderName: String?): Request {
        this.spiderName = spiderName
        return this
    }

    /**
     * http method
     *
     * @param httpMethod
     * @return
     */
    fun httpMethod(httpMethod: HttpMethod?): Request {
        this.httpMethod = httpMethod
        return this
    }

    /**
     * 网页使用的的字符集
     *
     * @param charset
     * @return
     */
    fun charset(charset: String?): Request {
        this.charset = charset
        return this
    }

    /**
     * 检查url是否重复，默认情况下为true表示需要检查。
     * 如果设置为false表示不需要检测url是否重复，此时可以多次请求该url。
     *
     * @param checkDuplicate
     * @return
     */
    fun checkDuplicate(checkDuplicate: Boolean): Request {
        this.checkDuplicate = checkDuplicate
        return this
    }

    /**
     * 是否保存cookie，默认情况下为false表示不保存cookie
     *
     * @param saveCookie
     * @return
     */
    fun saveCookie(saveCookie: Boolean): Request {
        this.saveCookie = saveCookie
        return this
    }

    /**
     * 是否 debug 模式下测试 request
     *
     * @param debug
     * @return
     */
    fun debug(debug: Boolean): Request {
        this.debug = debug
        return this
    }

    /**
     * @param sleepTime 每次请求url时先sleep一段时间，单位是milliseconds
     * @return
     */
    fun sleep(sleepTime: Long): Request {
        if (sleepTime > 0) {
            this.sleepTime = sleepTime
        }
        return this
    }

    /**
     * 每次请求url时先随机sleep一段时间，单位是milliseconds
     * @return
     */
    fun autoSleepTime(): Request {
        sleepTime = RandomUtils.nextLong(1000, 6000)
        return this
    }

    /**
     * @param downloadDelay 每次下载时先delay一段时间，单位是milliseconds
     * @return
     */
    fun downloadDelay(downloadDelay: Long): Request {
        if (downloadDelay > 0) {
            this.downloadDelay = downloadDelay
        }
        return this
    }

    /**
     * 每次下载时先随机delay一段时间，单位是milliseconds
     * @return
     */
    fun autoDownloadDelay(): Request {
        downloadDelay = RandomUtils.nextLong(1000, 6000)
        return this
    }

    fun domainDelay(domainDelay: Long): Request {
        if (domainDelay > 0) {
            this.domainDelay = domainDelay
        }
        return this
    }

    fun autoDomainDelay(): Request {
        domainDelay = RandomUtils.nextLong(1000, 6000)
        return this
    }

    fun header(name: String, value: String?): Request {
        header[name] = value
        return this
    }

    fun referer(referer: String?): Request {
        return header("Referer", referer)
    }

    fun addCookie(cookie: String?): Request {
        return header("Cookie", cookie)
    }

    fun putExtra(key: String, value: Any): Request {
        if (extras == null) {
            extras = NoEmptyHashMap()
        }
        extras!![key] = value
        return this
    }

    fun getExtra(key: String): Any? {
        return if (extras != null) extras!![key] else null
    }

    /**
     * 设置Request的优先级
     *
     * @param priority
     * @return
     */
    fun priority(priority: Long): Request {
        if (priority > 0) {
            this.priority = priority
        }
        return this
    }

    fun clearHeader() {
        val it: MutableIterator<Map.Entry<String, String?>> = header.entries.iterator()
        while (it.hasNext()) {
            it.next()
            it.remove()
        }
    }

    fun httpRequestBody(httpRequestBody: HttpRequestBody?): Request {
        this.httpRequestBody = httpRequestBody
        return this
    }

    fun beforeRequest(beforeRequest: BeforeRequest?): Request {
        this.beforeRequest = beforeRequest
        return this
    }

    fun afterRequest(afterRequest: AfterRequest?): Request {
        this.afterRequest = afterRequest
        return this
    }

    fun onErrorRequest(onErrorRequest: OnErrorRequest?): Request {
        this.onErrorRequest = onErrorRequest
        return this
    }

    /**
     * 在request之前做的事情
     */
    fun interface BeforeRequest {
        fun process(request: Request?)
    }

    /**
     * 在request之后做的事情
     */
    fun interface AfterRequest {
        fun process(page: Page?)
    }

    /**
     * 在request发生异常做的事情
     */
    fun interface OnErrorRequest {
        fun process(request: Request?)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Request{")
            .append("spiderName=").append(spiderName)
            .append(", url='").append(url).append('\'')
            .append(", method='").append(httpMethod).append('\'')
            .append(", userAgent=").append(userAgent)
            .append(", extras=").append(extras)
            .append(", priority=").append(priority)
            .append(", headers=").append(header.toString())
            .append(", sleepTime=").append(sleepTime)
            .append(", downloadDelay=").append(downloadDelay)
            .append(", domainDelay=").append(domainDelay)
        if (proxy != null) {
            sb.append(", proxy=").append(proxy!!.proxyStr)
        }
        return sb.append('}').toString()
    }
}