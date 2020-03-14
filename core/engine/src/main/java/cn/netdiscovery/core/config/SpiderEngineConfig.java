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

    private static class Holder {
        private static final SpiderEngineConfig instance = new SpiderEngineConfig();
    }

    private SpiderEngineConfig() {
        Config conf = ConfigFactory.load().getConfig("spiderEngine.config");
        port = conf.getInt("port");
        useMonitor = conf.getBoolean("useMonitor");
    }

    public static final SpiderEngineConfig getInsatance() {
        return SpiderEngineConfig.Holder.instance;
    }

    public int getPort() {
        return port;
    }

    public boolean isUseMonitor() {
        return useMonitor;
    }
}
