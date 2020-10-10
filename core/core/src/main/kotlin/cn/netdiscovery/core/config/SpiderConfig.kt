package cn.netdiscovery.core.config

import com.typesafe.config.ConfigFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.config.SpiderConfig
 * @author: Tony Shen
 * @date: 2020-10-10 16:41
 * @version: V1.0 <描述当前版本功能>
 */
object SpiderConfig {

    var autoProxy:Boolean = false
    var initialDelay: Long = 0
    var maxRetries = 0
    var retryDelayMillis: Long = 0
    var usePrintRequestPipeline = false
    var useConsolePipeline = false
    var sleepTime: Long = 0
    var autoSleepTime = false
    var downloadDelay: Long = 0
    var autoDownloadDelay = false
    var domainDelay: Long = 0
    var autoDomainDelay = false
    var queueType: String? = null
    var pipelineDelay: Long = 0
    var autoPipelineDelay = false
    var downloaderType: String? = null
    var keepAlive = false
    var reuseAddress = false
    var followRedirects = false
    var connectTimeout = 0
    var idleTimeout = 0
    var maxWaitQueueSize = 0


    init {
        try {
            val config = ConfigFactory.load().getConfig("spider.config")
            autoProxy = config.getBoolean("autoProxy")
            initialDelay = config.getLong("initialDelay")
            maxRetries = config.getInt("maxRetries")
            retryDelayMillis = config.getLong("retryDelayMillis")
            usePrintRequestPipeline = config.getBoolean("usePrintRequestPipeline")
            useConsolePipeline = config.getBoolean("useConsolePipeline")
        } catch (e: Exception) {
            autoProxy = false
            initialDelay = 0
            maxRetries = 3
            retryDelayMillis = 1000
            usePrintRequestPipeline = true
            useConsolePipeline = false
        }
        try {
            val requestConfig = ConfigFactory.load().getConfig("spider.request")
            sleepTime = requestConfig.getLong("sleepTime")
            autoSleepTime = requestConfig.getBoolean("autoSleepTime")
            downloadDelay = requestConfig.getLong("downloadDelay")
            autoDownloadDelay = requestConfig.getBoolean("autoDownloadDelay")
            domainDelay = requestConfig.getLong("domainDelay")
            autoDomainDelay = requestConfig.getBoolean("autoDomainDelay")
        } catch (e: Exception) {
            sleepTime = 0
            autoSleepTime = true
            downloadDelay = 0
            autoDownloadDelay = true
            domainDelay = 0
            autoDomainDelay = true
        }
        queueType = try {
            val queueConfig = ConfigFactory.load().getConfig("spider.queue")
            queueConfig.getString("type")
        } catch (e: Exception) {
            "default"
        }
        try {
            val pipelineConfig = ConfigFactory.load().getConfig("spider.pipeline")
            pipelineDelay = pipelineConfig.getLong("pipelineDelay")
            autoPipelineDelay = pipelineConfig.getBoolean("autoPipelineDelay")
        } catch (e: Exception) {
            pipelineDelay = 0
            autoPipelineDelay = false
        }
        try {
            val downloaderConfig = ConfigFactory.load().getConfig("spider.downloader")
            downloaderType = downloaderConfig.getString("type")
            val optionConfig = downloaderConfig.getConfig("vertx.options")
            keepAlive = optionConfig.getBoolean("keepAlive")
            reuseAddress = optionConfig.getBoolean("reuseAddress")
            followRedirects = optionConfig.getBoolean("followRedirects")
            connectTimeout = optionConfig.getInt("connectTimeout")
            idleTimeout = optionConfig.getInt("idleTimeout")
            maxWaitQueueSize = optionConfig.getInt("maxWaitQueueSize")
        } catch (e: Exception) {
            keepAlive = true
            reuseAddress = true
            followRedirects = true
            connectTimeout = 10000
            idleTimeout = 10
            maxWaitQueueSize = 10
        }
    }
}