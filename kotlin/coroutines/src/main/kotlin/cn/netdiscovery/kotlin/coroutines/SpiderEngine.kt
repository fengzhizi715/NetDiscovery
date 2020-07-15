package cn.netdiscovery.kotlin.coroutines

import cn.netdiscovery.core.config.Constant
import cn.netdiscovery.core.config.Constant.*
import cn.netdiscovery.core.config.SpiderEngineConfig
import cn.netdiscovery.core.domain.bean.SpiderJobBean
import cn.netdiscovery.core.quartz.ProxyPoolJob
import cn.netdiscovery.core.quartz.QuartzManager
import cn.netdiscovery.core.queue.Queue
import cn.netdiscovery.core.registry.Registry
import cn.netdiscovery.core.utils.UserAgent
import cn.netdiscovery.core.vertx.RegisterConsumer
import cn.netdiscovery.core.vertx.VertxManager
import com.cv4j.proxy.ProxyManager
import com.cv4j.proxy.ProxyPool
import com.cv4j.proxy.domain.Proxy
import com.safframework.tony.common.utils.IOUtils
import com.safframework.tony.common.utils.Preconditions
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.vertx.core.Verticle
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.micrometer.VertxPrometheusOptions
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by tony on 2018/8/8.
 */
class SpiderEngine private constructor(val queue: Queue? = null) {

    private var server: HttpServer? = null

    private var useMonitor = false

    private var registerConsumer: RegisterConsumer? = null

    private var registry: Registry? = null

    private var defaultHttpdPort = 8715 // SpiderEngine 默认的端口号

    private val count = AtomicInteger(0)

    private val spiders = ConcurrentHashMap<String, Spider>()

    private val jobs = ConcurrentHashMap<String, SpiderJobBean>()

    init {

        initSpiderEngine()
    }

    /**
     * 初始化爬虫引擎，加载ua列表
     */
    private fun initSpiderEngine() {

        val uaList = Constant.uaFiles

        if (Preconditions.isNotBlank(uaList)) {

            Arrays.asList(*uaList)
                    .parallelStream()
                    .forEach { name ->

                        try {
                            this.javaClass.getResourceAsStream(name)?.let {
                                val inputString = IOUtils.inputStream2String(it) // it 无须关闭，inputStream2String()方法里已经做了关闭流的操作
                                if (Preconditions.isNotBlank(inputString)) {
                                    val ss = inputString.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    if (ss.isNotEmpty()) {

                                        Arrays.asList(*ss).forEach {
                                            UserAgent.uas.add(it)
                                        }
                                    }
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

            com.cv4j.proxy.config.Constant.setUas(UserAgent.uas) // 让代理池也能够共享ua
        }

        try {
            defaultHttpdPort = SpiderEngineConfig.getInstance().port
            useMonitor = SpiderEngineConfig.getInstance().isUseMonitor
        } catch (e: ClassCastException) {
            defaultHttpdPort = 8715
            useMonitor = false
        }

        VertxManager.configVertx(VertxOptions().setMetricsOptions(
                MicrometerMetricsOptions()
                        .setPrometheusOptions(VertxPrometheusOptions().setEnabled(true))
                        .setEnabled(true)))
    }

    fun proxyList(proxies: List<Proxy>): SpiderEngine {

        ProxyPool.addProxyList(proxies)
        return this
    }

    fun setUseMonitor(useMonitor: Boolean): SpiderEngine {

        this.useMonitor = useMonitor
        return this
    }

    fun setRegistry(registry: Registry): SpiderEngine {

        this.registry = registry
        return this
    }

    /**
     * 添加爬虫到SpiderEngine，由SpiderEngine来管理
     *
     * @param spider
     * @return
     */
    fun addSpider(spider: Spider?): SpiderEngine {

        spider?.let {

            if (!spiders.containsKey(it.name)) {

                spiders[it.name] = it
            }
        }
        return this
    }

    /**
     * 在SpiderEngine中创建一个爬虫，使用SpiderEngine的Queue
     *
     * @param name
     * @return Spider
     */
    fun createSpider(name: String): Spider? {

        if (!spiders.containsKey(name)) {

            return Spider.create(this.queue).name(name).apply {

                spiders[name] = this
            }
        }

        return null
    }

    /**
     * 对各个爬虫的状态进行监测，并返回json格式。
     * 如果要使用此方法，必须要放在run()之前
     *
     * @param port
     */
    @JvmOverloads
    fun httpd(port: Int = defaultHttpdPort): SpiderEngine {

        defaultHttpdPort = port

        server = VertxManager.getVertx().createHttpServer()?.apply {

            val router = Router.router(VertxManager.getVertx())
            router.route().handler(BodyHandler.create())

            val routerHandler = RouterHandler(spiders, jobs, router, useMonitor)
            routerHandler.route()

            requestHandler{ router.accept(it) }.listen(port)
        }

        return this
    }

    /**
     * 注册 Vert.x eventBus 的消费者
     * @param registerConsumer
     * @return
     */
    fun registerConsumers(registerConsumer: RegisterConsumer): SpiderEngine {

        this.registerConsumer = registerConsumer
        return this
    }

    /**
     * 关闭HttpServer
     */
    fun closeHttpServer() {

        server?.let {
            it.close()
        }
    }

    /**
     * 启动SpiderEngine中所有的spider，让每个爬虫并行运行起来。
     *
     */
    fun run() {
        println("\r\n" +
                "   _   _      _   ____  _\n" +
                "  | \\ | | ___| |_|  _ \\(_)___  ___ _____   _____ _ __ _   _\n" +
                "  |  \\| |/ _ \\ __| | | | / __|/ __/ _ \\ \\ / / _ \\ '__| | | |\n" +
                "  | |\\  |  __/ |_| |_| | \\__ \\ (_| (_) \\ V /  __/ |  | |_| |\n" +
                "  |_| \\_|\\___|\\__|____/|_|___/\\___\\___/ \\_/ \\___|_|   \\__, |\n" +
                "                                                      |___/\n"+
                "  NetDiscovery is running ...\n"+
                "  Author: Tony Shen，Email: fengzhizi715@126.com");

        if (Preconditions.isNotBlank<Map<String, Spider>>(spiders)) {

            registry?.takeIf { it.provider!=null }?.let {

                it.register(it.provider,defaultHttpdPort)
            }

            registerConsumer?.let {
                it.process()
            }

            Flowable.fromIterable(spiders.values)
                    .parallel(spiders.values.size)
                    .runOn(Schedulers.io())
                    .map {
                        it.run()

                        it
                    }
                    .sequential()
                    .subscribe({ }, { it.printStackTrace() }, { })

            Runtime.getRuntime().addShutdownHook(Thread {
                println("stop all spiders")
                stopSpiders()
                QuartzManager.shutdownJobs()
            })
        }
    }

    /**
     * 基于爬虫的名字，从SpiderEngine中获取爬虫
     *
     * @param name
     */
    fun getSpider(name: String) = spiders[name]

    /**
     * 停止某个爬虫程序
     *
     * @param name
     */
    fun stopSpider(name: String) {

        spiders[name]?.stop()
    }

    /**
     * 停止所有的爬虫程序
     */
    fun stopSpiders() {

        if (Preconditions.isNotBlank(spiders)) {

            spiders.forEach { (_, spider) -> spider.stop() }
        }
    }

    /**
     * 给 ProxyPool 发起定时任务
     * @param proxyMap
     * @param cron cron表达式
     * @return
     */
    fun addProxyPoolJob(proxyMap: Map<String, Class<*>>, cron: String) {

        val jobName = PROXY_POOL_JOB_NAME + count.incrementAndGet()

        QuartzManager.addJob(jobName, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, ProxyPoolJob::class.java, cron, proxyMap)
    }

    /**
     * 需要在启动 SpiderEngine 之前，启动 ProxyPool
     */
    fun startProxyPool(proxyMap: Map<String, Class<*>>) {

        if (Preconditions.isNotBlank(proxyMap)) {

            ProxyPool.proxyMap = proxyMap

            ProxyManager.get()?.start()
        }
    }

    /**
     * 部署 Vert.x 的 Verticle，便于爬虫引擎的扩展
     * @param verticle
     */
    fun deployVerticle(verticle: Verticle) {
        VertxManager.deployVerticle(verticle)
    }

    companion object {

        @JvmStatic
        fun create() = SpiderEngine()

        @JvmStatic
        fun create(queue: Queue) = SpiderEngine(queue)
    }
}