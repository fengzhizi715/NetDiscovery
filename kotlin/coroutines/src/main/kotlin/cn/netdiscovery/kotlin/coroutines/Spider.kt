package cn.netdiscovery.kotlin.coroutines

import cn.netdiscovery.core.config.Constant
import cn.netdiscovery.core.config.SpiderConfig
import cn.netdiscovery.core.domain.Page
import cn.netdiscovery.core.domain.Request
import cn.netdiscovery.core.domain.Response
import cn.netdiscovery.core.downloader.Downloader
import cn.netdiscovery.core.downloader.file.FileDownloader
import cn.netdiscovery.core.downloader.urlconnection.UrlConnectionDownloader
import cn.netdiscovery.core.downloader.vertx.VertxDownloader
import cn.netdiscovery.core.exception.SpiderException
import cn.netdiscovery.core.parser.Parser
import cn.netdiscovery.core.parser.selector.Html
import cn.netdiscovery.core.parser.selector.Json
import cn.netdiscovery.core.pipeline.ConsolePipeline
import cn.netdiscovery.core.pipeline.Pipeline
import cn.netdiscovery.core.pipeline.PrintRequestPipeline
import cn.netdiscovery.core.queue.DefaultQueue
import cn.netdiscovery.core.queue.Queue
import cn.netdiscovery.core.queue.disruptor.DisruptorQueue
import cn.netdiscovery.core.rxjava.RetryWithDelay
import cn.netdiscovery.core.utils.SpiderUtils
import com.cv4j.proxy.ProxyPool
import com.safframework.tony.common.utils.IOUtils
import com.safframework.tony.common.utils.Preconditions
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.await
import org.apache.commons.lang3.RandomUtils
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by tony on 2018/8/8.
 */

class Spider private constructor(var queue: Queue = DefaultQueue()) {

    private var stat = AtomicInteger(SPIDER_STATUS_INIT)

    var name = "spider" // 爬虫的名字，默认使用spider

    private var parser: Parser?=null // 不能使用 lateinit 声明

    private val pipelines = LinkedList<Pipeline>()

    private var autoProxy = false

    private var initialDelay: Long = 0

    private var maxRetries = 3 // 重试次数

    private var retryDelayMillis: Long = 1000 // 重试等待的时间

    private var sleepTime: Long = 30000  // 默认30s

    private var requestSleepTime: Long = 0 // 默认0s

    private var autoSleepTime = false

    private var downloadDelay: Long = 0  // 默认0s

    private var autoDownloadDelay = false

    private var pipelineDelay: Long = 0  // 默认0s

    private var autoPipelineDelay = false

    private var domainDelay: Long = 0  // 默认0s

    private var autoDomainDelay = false

    @Volatile
    private var pause: Boolean = false

    private lateinit var pauseCountDown: CountDownLatch
    private val newRequestLock = ReentrantLock()
    private val newRequestCondition = newRequestLock.newCondition()

    private val compositeDisposable = CompositeDisposable()

    lateinit var downloader: Downloader

    val spiderStatus: Int
        get() = stat.get()

    init {

        try {
            val queueType = SpiderConfig.getInstance().queueType

            if (Preconditions.isNotBlank(queueType)) {

                when (queueType) {
                    Constant.QUEUE_TYPE_DEFAULT   -> this.queue = DefaultQueue()
                    Constant.QUEUE_TYPE_DISRUPTOR -> this.queue = DisruptorQueue()
                }
            }
        } catch (e: ClassCastException) {
            println(e.message)
        }

        if (queue == null) {
            queue = DefaultQueue()
        }

        initSpiderConfig()
    }

    /**
     * 从 application.conf 中获取配置，并依据这些配置来初始化爬虫
     */
    private fun initSpiderConfig() {
        autoProxy = SpiderConfig.getInstance().isAutoProxy
        initialDelay = SpiderConfig.getInstance().initialDelay
        maxRetries = SpiderConfig.getInstance().maxRetries
        retryDelayMillis = SpiderConfig.getInstance().retryDelayMillis

        requestSleepTime = SpiderConfig.getInstance().sleepTime
        autoSleepTime = SpiderConfig.getInstance().isAutoSleepTime
        downloadDelay = SpiderConfig.getInstance().downloadDelay
        autoDownloadDelay = SpiderConfig.getInstance().isAutoDownloadDelay
        domainDelay = SpiderConfig.getInstance().domainDelay
        autoDomainDelay = SpiderConfig.getInstance().isAutoDomainDelay

        pipelineDelay = SpiderConfig.getInstance().pipelineDelay
        autoPipelineDelay = SpiderConfig.getInstance().isAutoPipelineDelay

        val downloaderType = SpiderConfig.getInstance().downloaderType

        if (Preconditions.isNotBlank(downloaderType)) {
            when (downloaderType) {
                Constant.DOWNLOAD_TYPE_VERTX          -> this.downloader = VertxDownloader()
                Constant.DOWNLOAD_TYPE_URL_CONNECTION -> this.downloader = UrlConnectionDownloader()
                Constant.DOWNLOAD_TYPE_FILE           -> this.downloader = FileDownloader()
            }
        }

        val usePrintRequestPipeline = SpiderConfig.getInstance().isUsePrintRequestPipeline

        if (usePrintRequestPipeline) {
            this.pipelines.add(PrintRequestPipeline()) // 默认使用 PrintRequestPipeline
        }

        val useConsolePipeline = SpiderConfig.getInstance().isUseConsolePipeline

        if (useConsolePipeline) {
            this.pipelines.add(ConsolePipeline())  // 默认使用 ConsolePipeline
        }
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
                    .forEach {
                        pushToQueue(it,charset)
                    }

            signalNewRequest()
        }

        return this
    }

    fun url(vararg urls: String): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            Arrays.asList(*urls)
                    .stream()
                    .forEach {
                        pushToQueue(it,null)
                    }

            signalNewRequest()
        }

        return this
    }

    fun url(charset: Charset, urls: List<String>): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach {
                pushToQueue(it,charset)
            }

            signalNewRequest()
        }

        return this
    }

    fun url(urls: List<String>): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(urls)) {

            urls.forEach {
                pushToQueue(it,null)
            }

            signalNewRequest()
        }

        return this
    }

    private fun pushToQueue(url: String, charset: Charset?) {
        val request = Request(url, name)
        if (charset != null) {
            request.charset(charset.name())
        }
        if (autoSleepTime) {
            request.autoSleepTime()
        } else {
            request.sleep(requestSleepTime)
        }
        if (autoDownloadDelay) {
            request.autoDownloadDelay()
        } else {
            request.downloadDelay(downloadDelay)
        }
        if (autoDomainDelay) {
            request.autoDomainDelay()
        } else {
            request.domainDelay(domainDelay)
        }
        queue.push(request)
    }

    fun request(vararg requests: Request): Spider {

        checkIfRunning()

        if (Preconditions.isNotBlank(requests)) {

            Arrays.asList(*requests)
                    .stream()
                    .forEach { queue.push(it.spiderName(name)) }

            signalNewRequest()
        }

        return this
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period 每隔一定的时间提交一次request
     * @param url
     * @return
     */
    fun repeatRequest(period: Long, url: String): Spider = repeatRequest(period, url, Constant.UTF_8)

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
                                val request = Request(url).apply {

                                    checkDuplicate(false)
                                    spiderName(name)
                                    sleep(period) // 使用 repeatRequest() 时，autoSleepTime 属性可以不必关注
                                    charset(charset)
                                    if (autoDownloadDelay) {
                                        autoDownloadDelay()
                                    } else {
                                        downloadDelay(downloadDelay)
                                    }
                                    if (autoDomainDelay) {
                                        autoDomainDelay()
                                    } else {
                                        domainDelay(domainDelay)
                                    }
                                }

                                queue.push(request)

                                signalNewRequest()
                            }
                        })

        return this
    }

    /**
     * 可以重复提交request，用于实现定时任务，使用该方法时需要跟initialDelay一起配合使用。
     * @param period
     * @param request
     * @return
     */
    fun repeatRequest(period: Long, request: Request?): Spider {

        checkIfRunning()

        if (request != null) {

            compositeDisposable
                    .add(Flowable.interval(period, TimeUnit.MILLISECONDS)
                            .onBackpressureBuffer()
                            .subscribe {
                                if (!pause) {

                                    request.apply {

                                        if (sleepTime == 0L || sleepTime != period) {
                                            sleep(period) // 使用 repeatRequest() 时，autoSleepTime 属性可以不必关注
                                        }

                                        if (downloadDelay == 0L) {
                                            if (autoDownloadDelay) {
                                                autoDownloadDelay()
                                            } else {
                                                downloadDelay(downloadDelay)
                                            }
                                        }

                                        if (domainDelay == 0L) {
                                            if (autoDomainDelay) {
                                                autoDomainDelay()
                                            } else {
                                                domainDelay(domainDelay)
                                            }
                                        }

                                        spiderName(name)
                                    }


                                    queue.push(request)

                                    signalNewRequest()
                                }
                            })
        }

        return this
    }

    fun initialDelay(initialDelay: Long): Spider {

        checkIfRunning()

        if (initialDelay > 0) {
            this.initialDelay = initialDelay
        }

        return this
    }

    fun maxRetries(maxRetries: Int): Spider {

        checkIfRunning()

        if (maxRetries > 0) {
            this.maxRetries = maxRetries
        }

        return this
    }

    fun retryDelayMillis(retryDelayMillis: Long): Spider {

        checkIfRunning()

        if (retryDelayMillis > 0) {
            this.retryDelayMillis = retryDelayMillis
        }

        return this
    }

    fun sleepTime(sleepTime: Long): Spider {

        checkIfRunning()

        if (sleepTime > 0) {
            this.sleepTime = sleepTime
        }

        return this
    }

    fun downloader(downloader: Downloader?): Spider {

        checkIfRunning()

        downloader?.let {
            this.downloader = it
        }

        return this
    }

    fun parser(parser: Parser?): Spider {

        checkIfRunning()

        parser?.let {
            this.parser = it
        }

        return this
    }

    fun pipeline(pipeline: Pipeline?): Spider {

        checkIfRunning()
        
        pipeline?.let {

            if (it.pipelineDelay == 0L) {
                if (autoPipelineDelay) {
                    pipelineDelay = RandomUtils.nextLong(1000, 6000)
                }
                it.pipelineDelay = pipelineDelay
            }

            this.pipelines.add(it)
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

    fun waitNewRequest() {
        newRequestLock.lock()
        
        try {
            newRequestCondition.await()
        } catch (e: InterruptedException) {
        } finally {
            newRequestLock.unlock()
        }
    }

    fun signalNewRequest() {
        newRequestLock.lock()

        try {
            newRequestCondition.signalAll()
        } finally {
            newRequestLock.unlock()
        }
    }

    fun run() {

        runBlocking {

            checkRunningStat()

            initialDelay()

            if (downloader == null) { // 如果downloader为空，则使用默认的VertxDownloader

                downloader = VertxDownloader()
            }

            println("Spider $name started!")

            while (spiderStatus != SPIDER_STATUS_STOPPED) {

                //暂停抓取
                if (pause && pauseCountDown != null) {
                    try {
                        this@Spider.pauseCountDown.await()
                    } catch (e: InterruptedException) {
//                        log.error("can't pause : ", e)
                    }

                    initialDelay()
                }

                // 从消息队列中取出request
                val request = queue.poll(name)

                if (request == null) {
                    waitNewRequest()
                } else {
                    if (request.sleepTime > 0) {

                        delay(request.sleepTime)
                    }

                    // 如果autoProxy打开并且request.getProxy()==null时，则从ProxyPool中取Proxy
                    if (autoProxy && request.proxy == null) {

                        val proxy = ProxyPool.getProxy()

                        if (proxy != null && SpiderUtils.checkProxy(proxy)) {
                            request.proxy(proxy)
                        }
                    }

                    // request请求之前的处理
                    if (request.beforeRequest != null) {

                        request.beforeRequest.process(request)
                    }

                    // request正在处理
                    val download = downloader.download(request)
                            .retryWhen(
                                RetryWithDelay<Response>(
                                    3,
                                    1000,
                                    request
                                )
                            )
                            .await()

                    download?.run {

                        val page = Page()
                        page.request = request
                        page.url = request.url
                        page.statusCode = statusCode

                        if (SpiderUtils.isTextType(contentType)) { // text/html

                            page.html = Html(content)
                        } else if (SpiderUtils.isApplicationJSONType(contentType)) { // application/json

                            // 将json字符串转化成Json对象，放入Page的"RESPONSE_JSON"字段。之所以转换成Json对象，是因为Json提供了toObject()，可以转换成具体的class。
                            page.putField(Constant.RESPONSE_JSON, Json(String(content)))
                        } else if (SpiderUtils.isApplicationJSONPType(contentType)) { // application/javascript

                            // 转换成字符串，放入Page的"RESPONSE_JSONP"字段。
                            // 由于是jsonp，需要开发者在Pipeline中自行去掉字符串前后的内容，这样就可以变成json字符串了。
                            page.putField(Constant.RESPONSE_JSONP, String(content))
                        } else {

                            page.putField(Constant.RESPONSE_RAW, `is`) // 默认情况，保存InputStream
                        }

                        page
                    }?.apply {

                        parser?.let {
                            it.process(this)
                        }

                    }?.apply {

                        if (!this.resultItems.isSkip && Preconditions.isNotBlank(pipelines)) {

                            pipelines.stream().forEach { pipeline -> pipeline.process(resultItems) }
                        }

                    }?.apply {

                        println(url)

                        request.afterRequest?.let {
                            it.process(this)
                        }

                        signalNewRequest()
                    }
                }
            }

            stopSpider(downloader) // 爬虫停止
        }
    }

    private fun checkIfRunning() {

        if (spiderStatus == SPIDER_STATUS_RUNNING) {
            throw SpiderException("Spider $name is already running!")
        }
    }

    private fun checkRunningStat() {

        while (true) {

            val statNow = spiderStatus
            if (statNow == SPIDER_STATUS_RUNNING) {
                throw SpiderException("Spider $name is already running!")
            }

            if (stat.compareAndSet(statNow, SPIDER_STATUS_RUNNING)) {
                break
            }
        }
    }

    private fun stopSpider(downloader: Downloader?) {

        IOUtils.closeQuietly(downloader)

        stop()
    }

    fun stop() {

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            println("Spider $name stop success!")
        }
    }

    fun forceStop() {

        compositeDisposable.clear()

        if (stat.compareAndSet(SPIDER_STATUS_RUNNING, SPIDER_STATUS_STOPPED)) { // 停止爬虫的状态
            println("Spider $name force stop success!")
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
            delay(initialDelay)
        }
    }

    companion object {

        val SPIDER_STATUS_INIT = 0
        val SPIDER_STATUS_RUNNING = 1
        val SPIDER_STATUS_PAUSE = 2
        val SPIDER_STATUS_RESUME = 3
        val SPIDER_STATUS_STOPPED = 4

        @JvmStatic
        fun create() = Spider()

        @JvmStatic
        fun create(queue: Queue?) = if (queue != null) Spider(queue) else Spider()
    }
}