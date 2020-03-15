package cn.netdiscovery.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @FileName: cn.netdiscovery.config.SpiderConfig
 * @author: Tony Shen
 * @date: 2020-03-15 12:19
 * @version: V1.0 <描述当前版本功能>
 */
public class SpiderConfig {

    private boolean autoProxy;
    private int initialDelay;
    private int maxRetries;
    private int retryDelayMillis;
    private boolean  usePrintRequestPipeline;
    private boolean useConsolePipeline;

    private SpiderConfig() {

        try {
            Config config = ConfigFactory.load().getConfig("spider.config");
            autoProxy = config.getBoolean("autoProxy");
            initialDelay = config.getInt("initialDelay");
            maxRetries = config.getInt("maxRetries");
            retryDelayMillis = config.getInt("retryDelayMillis");
            usePrintRequestPipeline = config.getBoolean("usePrintRequestPipeline");
            useConsolePipeline = config.getBoolean("useConsolePipeline");
        } catch (Exception e) {
            autoProxy = false;
            initialDelay = 0;
            maxRetries = 3;
            retryDelayMillis = 1000;
            usePrintRequestPipeline = true;
            useConsolePipeline = false;
        }
    }

    public boolean isAutoProxy() {
        return autoProxy;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public boolean isUsePrintRequestPipeline() {
        return usePrintRequestPipeline;
    }

    public boolean isUseConsolePipeline() {
        return useConsolePipeline;
    }

    public static final SpiderConfig getInsatance() {
        return SpiderConfig.Holder.instance;
    }

    private static class Holder {
        private static final SpiderConfig instance = new SpiderConfig();
    }
}
