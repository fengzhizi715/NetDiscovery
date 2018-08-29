package com.cv4j.netdiscovery.coroutines

import com.cv4j.netdiscovery.core.config.Constant
import com.cv4j.netdiscovery.core.domain.Page
import com.cv4j.netdiscovery.core.domain.Request
import com.cv4j.netdiscovery.core.domain.Response
import com.cv4j.netdiscovery.core.downloader.Downloader
import com.cv4j.netdiscovery.core.downloader.vertx.VertxDownloader
import com.cv4j.netdiscovery.core.exception.SpiderException
import com.cv4j.netdiscovery.core.parser.Parser
import com.cv4j.netdiscovery.core.parser.selector.Html
import com.cv4j.netdiscovery.core.parser.selector.Json
import com.cv4j.netdiscovery.core.pipeline.Pipeline
import com.cv4j.netdiscovery.core.queue.DefaultQueue
import com.cv4j.netdiscovery.core.queue.Queue
import com.cv4j.netdiscovery.core.utils.RetryWithDelay
import com.cv4j.netdiscovery.core.utils.Utils
import com.cv4j.proxy.ProxyPool
import com.safframework.tony.common.utils.IOUtils
import com.safframework.tony.common.utils.Preconditions
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.rx2.await
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by tony on 2018/8/8.
 */

class Spider private constructor(queue: Queue? = DefaultQueue()) {

    protected var stat = AtomicInteger(SPIDER_STATUS_INIT)

    var name = "spider" // 爬虫的名字，默认使用spider

    private var parser: Parser? = null

    private val pipelines = LinkedHashSet<Pipeline>()

    var queue: Queue

    private var autoProxy = false

    private var initialDelay: Long = 0

    private var pause: Boolean = false
    private lateinit var pauseCountDown: CountDownLatch

    private val compositeDisposable = CompositeDisposable()

    var downloader: Downloader

    val spiderStatus: Int
        get() = stat.get()

    init {

        if (queue != null) {
            this.queue = queue
        } else {
            this.queue = DefaultQueue()
        }

        downloader = VertxDownloader()
    }

    fun name(name: String): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(name)) {
            this.name = name
        }

        return this
    }

    fun url(charset: Charset, vararg urls: String): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(*urls)
                    .stream()
                    .forEach { url ->
                        val request = Request(url, name)
                        request.charset(charset.name())
                        queue.push(request)
                    }
        }

        return this
    }

    fun url(vararg urls: String): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(*urls)
                    .stream()
                    .forEach { url -> queue.push(Request(url, name)) }
        }

        return this
    }

    fun url(charset: Charset, urls: List<String>): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach { url ->
                val request = Request(url, name)
                request.charset(charset.name())
                queue.push(request)
            }
        }

        return this
    }

    fun url(urls: List<String>): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach { url -> queue.push(Request(url, name)) }
        }

        return this
    }

    fun request(vararg requests: Request): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(requests)) {

            Arrays.asList(*requests)
                    .stream()
                    .forEach { request -> queue.push(request.spiderName(name)) }
        }

        return this
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period 每隔一定的时间提交一次request
     * @param url
     * @return
     */
    fun repeatRequest(period: Long, url: String): Spider {

        checkIfRunning()

        compositeDisposable
                .add(Flowable.interval(period, TimeUnit.MILLISECONDS)
                        .onBackpressureBuffer()
                        .subscribe {

                            if (!pause) {
                                val request = Request(url)
                                request.checkDuplicate(false)
                                request.spiderName(name)
                                request.sleep(period)
                                queue.push(request)
                            }
                        })

        return this
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period
     * @param url
     * @param charset 字符集
     * @return
     */
    fun repeatRequest(period: Long, url: String, charset: String): Spider {

        checkIfRunning()

        compositeDisposable
                .add(Flowable.interval(period, TimeUnit.MILLISECONDS)
                        .onBackpressureBuffer()
                        .subscribe {
                            if (!pause) {
                                val request = Request(url)
                                request.checkDuplicate(false)
                                request.spiderName(name)
                                request.sleep(period)
                                request.charset(charset)
                                queue.push(request)
                            }
                        })

        return this
    }

    fun initialDelay(initialDelay: Long): Spider {

        checkIfRunning()

        if (initialDelay > 0) {
            this.initialDelay = initialDelay
        }

        return this
    }

    fun downloader(downloader: Downloader?): Spider {

        checkIfRunning()

        if (downloader != null) {
            this.downloader = downloader
        }

        return this
    }

    fun parser(parser: Parser?): Spider {

        checkIfRunning()

        if (parser != null) {
            this.parser = parser
        }

        return this
    }

    fun pipeline(pipeline: Pipeline?): Spider {

        checkIfRunning()

        if (pipeline != null) {
            this.pipelines.add(pipeline)
        }

        return this
    }

    fun clearPipeline(): Spider {

        checkIfRunning()
        this.pipelines.clear()
        return this
    }

    /**
     * 是否自动获取代理，如果是的话可以从代理池组件中获取代理
     * @param autoProxy
     * @return
     */
    @JvmOverloads
    fun autoProxy(autoProxy: Boolean = true): Spider {

        checkIfRunning()
        this.autoProxy = autoProxy
        return this
    }

    fun run() {

        runBlocking(CommonPool) {


            checkRunningStat()

            initialDelay()

            try {
                while (spiderStatus != SPIDER_STATUS_STOPPED) {

                    //暂停抓取
                    if (pause) {
                        try {
                            this@Spider.pauseCountDown.await()
                        } catch (e: InterruptedException) {
//                        log.error("can't pause : ", e)
                        }

                        initialDelay()
                    }

                    // 从消息队列中取出request
                    val request = queue.poll(name)

                    if (request != null) {

                        if (request.sleepTime > 0) {

                            delay(request.sleepTime,TimeUnit.MILLISECONDS)
                        }

                        // 如果autoProxy打开并且request.getProxy()==null时，则从ProxyPool中取Proxy
                        if (autoProxy && request.proxy == null) {

                            val proxy = ProxyPool.getProxy()

                            if (proxy != null && Utils.checkProxy(proxy)) {
                                request.proxy(proxy)
                            }
                        }

                        // request请求之前的处理
                        if (request.beforeRequest != null) {

                            request.beforeRequest.process(request)
                        }

                        // request正在处理
                        val download = downloader.download(request)
                                .retryWhen(RetryWithDelay<Response>(3,1000,request.url))
                                .await()

                        download?.run {

                            val page = Page()
                            page.request = request
                            page.url = request.url
                            page.statusCode = statusCode

                            if (Utils.isTextType(contentType)) { // text/html

                                page.html = Html(content)
                            } else if (Utils.isApplicationJSONType(contentType)) { // application/json

                                // 将json字符串转化成Json对象，放入Page的"RESPONSE_JSON"字段。之所以转换成Json对象，是因为Json提供了toObject()，可以转换成具体的class。
                                page.putField(Constant.RESPONSE_JSON, Json(String(content)))
                            } else if (Utils.isApplicationJSONPType(contentType)) { // application/javascript

                                // 转换成字符串，放入Page的"RESPONSE_JSONP"字段。
                                // 由于是jsonp，需要开发者在Pipeline中自行去掉字符串前后的内容，这样就可以变成json字符串了。
                                page.putField(Constant.RESPONSE_JSONP, String(content))
                            } else {

                                page.putField(Constant.RESPONSE_RAW, `is`) // 默认情况，保存InputStream
                            }

                            page
                        }?.apply {

                            if (parser != null) {

                                parser!!.process(this)
                            }

                        }?.apply {

                            if (Preconditions.isNotBlank(pipelines)) {

                                pipelines.stream()
                                        .forEach { pipeline -> pipeline.process(resultItems) }
                            }

                        }?.apply {

                            println(url)

                            if (request.afterRequest != null) {

                                request.afterRequest.process(this)
                            }
                        }
                    } else {

                        break
                    }
                }
            } finally {

                stopSpider(downloader) // 爬虫停止
            }
        }
    }

    private fun checkIfRunning() {

        if (spiderStatus == SPIDER_STATUS_RUNNING) {
            throw SpiderException(String.format("Spider %s is already running!", name))
        }
    }

    private fun checkRunningStat() {

        while (true) {

            val statNow = spiderStatus
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw SpiderException(String.format("Spider %s is already running!", name))
            }

            if (stat.compareAndSet(statNow, SPIDER_STATUS_RUNNING)) {
                break
            }
        }
    }

    private fun stopSpider(downloader: Downloader?) {

        if (downloader != null) {
            IOUtils.closeQuietly(downloader)
        }

        stop()
    }

    fun stop() {

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            println(String.format("Spider %s stop success!", name))
        }
    }

    fun forceStop() {

        compositeDisposable.clear()

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            println(String.format("Spider %s force stop success!", name))
        }
    }

    /**
     * 爬虫暂停，当前正在抓取的请求会继续抓取完成，之后的请求会等到resume的调用才继续抓取
     */
    fun pause() {
        this.pauseCountDown = CountDownLatch(1)
        this.pause = true
        stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_PAUSE)
    }

    /**
     * 爬虫重新开始
     */
    fun resume() {

        if (stat.get() == SPIDER_STATUS_PAUSE) {
            this.pauseCountDown.countDown()
            this.pause = false
            stat.compareAndSet(SPIDER_STATUS_PAUSE, SPIDER_STATUS_RUNNING)
        }
    }

    private suspend fun initialDelay() {

        if (initialDelay > 0) {
            delay(initialDelay,TimeUnit.MILLISECONDS)

        }
    }

    companion object {

        val SPIDER_STATUS_INIT = 0
        val SPIDER_STATUS_RUNNING = 1
        val SPIDER_STATUS_PAUSE = 2
        val SPIDER_STATUS_RESUME = 3
        val SPIDER_STATUS_STOPPED = 4

        @JvmStatic
        fun create(): Spider {

            return Spider()
        }

        @JvmStatic
        fun create(queue: Queue?): Spider {

            return if (queue != null) Spider(queue) else Spider()
        }
    }
}