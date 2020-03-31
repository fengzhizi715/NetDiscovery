package cn.netdiscovery.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;

/**
 * @FileName: cn.netdiscovery.config.SpiderEngineConfig
 * @author: Tony Shen
 * @date: 2020-03-14 20:58
 * @version: V1.0 <描述当前版本功能>
 */
@Getter
public class SpiderEngineConfig {

    private int port;
    private boolean useMonitor;
    private String zkStr;
    private String zkPath;
    private String etcdStr;
    private String etcdPath;

    private SpiderEngineConfig() {

        try {
            Config config = ConfigFactory.load().getConfig("spiderEngine.config");
            port = config.getInt("port");
            useMonitor = config.getBoolean("useMonitor");
        } catch (Exception e) {
            port = 8715;
            useMonitor = false;
        }

        try {
            Config zkConfig = ConfigFactory.load().getConfig("spiderEngine.registry.zookeeper");
            zkStr = zkConfig.getString("zkStr");
            zkPath = zkConfig.getString("zkPath");
        } catch (Exception e) {
            zkStr = "localhost:2181";
            zkPath = "/netdiscovery";
        }

        try {
            Config etcdConfig = ConfigFactory.load().getConfig("spiderEngine.registry.etcd");
            etcdStr = etcdConfig.getString("etcdStr");
            etcdPath = etcdConfig.getString("zkPath");
        } catch (Exception e) {
            etcdStr = "http://127.0.0.1:2379";
            etcdPath = "/netdiscovery";
        }
    }

    public static final SpiderEngineConfig getInstance() {
        return SpiderEngineConfig.Holder.instance;
    }

    private static class Holder {
        private static final SpiderEngineConfig instance = new SpiderEngineConfig();
    }
}
