package cn.netdiscovery.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @FileName: cn.netdiscovery.core.config.SpiderEngineConfig
 * @author: Tony Shen
 * @date: 2020-03-14 20:58
 * @version: V1.0 <描述当前版本功能>
 */
public class SpiderEngineConfig {

    private int port;
    private boolean useMonitor;
    private String zkStr;
    private String zkPath;

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
    }

    public int getPort() {
        return port;
    }

    public boolean isUseMonitor() {
        return useMonitor;
    }

    public String getZkStr() {
        return zkStr;
    }

    public String getZkPath() {
        return zkPath;
    }

    public static final SpiderEngineConfig getInsatance() {
        return SpiderEngineConfig.Holder.instance;
    }

    private static class Holder {
        private static final SpiderEngineConfig instance = new SpiderEngineConfig();
    }
}
