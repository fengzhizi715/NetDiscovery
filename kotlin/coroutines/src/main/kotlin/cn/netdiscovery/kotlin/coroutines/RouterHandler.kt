package cn.netdiscovery.kotlin.coroutines

import cn.netdiscovery.core.config.Constant.*
import cn.netdiscovery.core.domain.bean.SpiderBean
import cn.netdiscovery.core.domain.bean.SpiderJobBean
import cn.netdiscovery.core.domain.response.JobsResponse
import cn.netdiscovery.core.domain.response.SpiderResponse
import cn.netdiscovery.core.domain.response.SpiderStatusResponse
import cn.netdiscovery.core.domain.response.SpidersResponse
import cn.netdiscovery.core.utils.SerializableUtils
import cn.netdiscovery.core.vertx.VertxManager
import cn.netdiscovery.kotlin.coroutines.extension.pushToRunninSpider
import com.safframework.tony.common.utils.Preconditions
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.micrometer.PrometheusScrapingHandler
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

/**
 * Created by tony on 2019-08-12.
 */
class RouterHandler(private val spiders: Map<String, Spider>, private val jobs: Map<String, SpiderJobBean>, private val router: Router, private val useMonitor: Boolean) {

    fun route() {

        // 检测 SpiderEngine 的健康状况
        router.route(ROUTER_HEALTH).handler { routingContext ->

            val response = routingContext.response()
            response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

            response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.Ok))
        }

        router.route(ROUTER_METRICS).handler(PrometheusScrapingHandler.create())

        if (Preconditions.isNotBlank(spiders)) {

            // 显示容器下所有爬虫的信息
            router.route(ROUTER_SPIDERS).handler { routingContext ->

                val response = routingContext.response()
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

                val list = ArrayList<SpiderBean>()

                var spider: Spider? = null
                var entity: SpiderBean? = null

                for ((_, value) in spiders) {

                    spider = value

                    entity = SpiderBean().apply {
                        spiderName = spider.name
                        spiderStatus = spider.spiderStatus
                        leftRequestSize = spider.queue.getLeftRequests(spider.name)
                        totalRequestSize = spider.queue.getTotalRequests(spider.name)
                        consumedRequestSize = this.totalRequestSize - this.leftRequestSize
                        queueType = spider.queue.javaClass.simpleName
                        downloaderType = spider.downloader.javaClass.simpleName
                    }

                    list.add(entity)
                }

                val spidersResponse = SpidersResponse().apply {
                    code = OK_STATUS_CODE
                    message = SUCCESS
                    data = list
                }

                // 写入响应并结束处理
                response.end(SerializableUtils.toJson(spidersResponse))
            }

            // 根据爬虫的名称获取爬虫的详情
            router.route(ROUTER_SPIDER_DETAIL).handler { routingContext ->

                val response = routingContext.response()
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

                val spiderName = routingContext.pathParam("spiderName")

                if (Preconditions.isNotBlank(spiderName) && spiders[spiderName] != null) {

                    val spider = spiders[spiderName]!!

                    val entity = SpiderBean().apply {
                        this.spiderName = spiderName
                        spiderStatus = spider.spiderStatus
                        leftRequestSize = spider.queue.getLeftRequests(spiderName)
                        totalRequestSize = spider.queue.getTotalRequests(spiderName)
                        consumedRequestSize = totalRequestSize - leftRequestSize
                        queueType = spider.queue.javaClass.simpleName
                        downloaderType = spider.downloader.javaClass.simpleName
                    }

                    val spiderResponse = SpiderResponse().apply {
                        code = OK_STATUS_CODE
                        message = SUCCESS
                        data = entity
                    }

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderResponse))
                } else {
                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound))
                }
            }

            // 修改单个爬虫的状态
            router.post(ROUTER_SPIDER_STATUS).handler { routingContext ->

                val response = routingContext.response()
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

                val spiderName = routingContext.pathParam("spiderName")

                if (Preconditions.isNotBlank(spiderName) && spiders[spiderName] != null) {

                    val json = routingContext.bodyAsJson
                    var spiderStatusResponse: SpiderStatusResponse? = null

                    val spider = spiders[spiderName]!!

                    if (json != null) {

                        val status = json.getInteger("status")!!

                        spiderStatusResponse = SpiderStatusResponse()

                        when (status) {

                            Spider.SPIDER_STATUS_PAUSE -> {
                                spider.pause()
                                spiderStatusResponse.data = String.format("SpiderEngine pause Spider %s success", spider.name)
                            }

                            Spider.SPIDER_STATUS_RESUME -> {
                                spider.resume()
                                spiderStatusResponse.data = String.format("SpiderEngine resume Spider %s success", spider.name)
                            }

                            Spider.SPIDER_STATUS_STOPPED -> {
                                spider.forceStop()
                                spiderStatusResponse.data = String.format("SpiderEngine stop Spider %s success", spider.name)
                            }
                        }
                    }

                    spiderStatusResponse?.apply {
                        code = OK_STATUS_CODE
                        message = SUCCESS
                    }

                    // 写入响应并结束处理
                    response.end(SerializableUtils.toJson(spiderStatusResponse))
                } else {
                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound))
                }

            }

            // 添加新的url任务到某个正在运行中的爬虫
            router.post(ROUTER_SPIDER_PUSH).handler { routingContext ->

                val response = routingContext.response()
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

                val spiderName = routingContext.pathParam("spiderName")

                if (Preconditions.isNotBlank(spiderName) && spiders[spiderName] != null) {

                    val json = routingContext.bodyAsJson

                    val url = json.getString("url")

                    val spider = spiders[spiderName]!!
                    spider.queue.pushToRunninSpider(url, spider)

                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse("待抓取的url已经放入queue中")))
                } else {
                    response.end(SerializableUtils.toJson(cn.netdiscovery.core.domain.response.HttpResponse.SpiderNotFound))
                }

            }

            // 显示所有爬虫的定时任务
            router.route(ROUTER_JOBS).handler { routingContext ->

                val response = routingContext.response()
                response.putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)

                val list = ArrayList<SpiderJobBean>()

                list.addAll(jobs.values)

                val jobsResponse = JobsResponse().apply {
                    code = OK_STATUS_CODE
                    message = SUCCESS
                    data = list
                }

                // 写入响应并结束处理
                response.end(SerializableUtils.toJson(jobsResponse))
            }

            if (useMonitor) { // 是否使用 agent

                // The web server handler
                router.route().handler(StaticHandler.create().setCachingEnabled(false))

                // The proxy handler
                val client = WebClient.create(VertxManager.getVertx())

                var localhost: InetAddress? = null
                try {
                    localhost = InetAddress.getLocalHost()
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }

                val get = client.get(8081, localhost!!.hostAddress, "/netdiscovery/dashboard/")
                router.get("/dashboard").handler { ctx ->
                    get.send { ar ->
                        if (ar.succeeded()) {
                            val result = ar.result()
                            ctx.response()
                                    .setStatusCode(result.statusCode())
                                    .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                                    .end(result.body())
                        } else {
                            ctx.fail(ar.cause())
                        }
                    }
                }
            }
        }
    }

}