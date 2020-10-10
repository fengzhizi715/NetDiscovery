package cn.netdiscovery.core.config

import com.typesafe.config.ConfigFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.config.SpiderEngineConfig
 * @author: Tony Shen
 * @date: 2020-10-10 17:52
 * @version: V1.0 <描述当前版本功能>
 */
object SpiderEngineConfig {

    var port = 0
    var useMonitor = false
    var zkStr: String? = null
    var zkPath: String? = null
    var etcdStr: String? = null
    var etcdPath: String? = null

    init {
        try {
            val config = ConfigFactory.load().getConfig("spiderEngine.config")
            port = config.getInt("port")
            useMonitor = config.getBoolean("useMonitor")
        } catch (e: Exception) {
            port = 8715
            useMonitor = false
        }
        try {
            val zkConfig = ConfigFactory.load().getConfig("spiderEngine.registry.zookeeper")
            zkStr = zkConfig.getString("zkStr")
            zkPath = zkConfig.getString("zkPath")
        } catch (e: Exception) {
            zkStr = "localhost:2181"
            zkPath = "/netdiscovery"
        }
        try {
            val etcdConfig = ConfigFactory.load().getConfig("spiderEngine.registry.etcd")
            etcdStr = etcdConfig.getString("etcdStr")
            etcdPath = etcdConfig.getString("zkPath")
        } catch (e: Exception) {
            etcdStr = "http://127.0.0.1:2379"
            etcdPath = "/netdiscovery"
        }
    }
}