package cn.netdiscovery.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;

/**
 * @FileName: cn.netdiscovery.config.SpiderConfig
 * @author: Tony Shen
 * @date: 2020-03-15 12:19
 * @version: V1.0 <描述当前版本功能>
 */
@Getter
public class SpiderConfig {

    private boolean autoProxy;
    private int initialDelay;
    private int maxRetries;
    private int retryDelayMillis;
    private boolean usePrintRequestPipeline;
    private boolean useConsolePipeline;

    private int sleepTime;
    private boolean autoSleepTime;
    private int  downloadDelay;
    private boolean autoDownloadDelay;
    private int domainDelay;
    private boolean autoDomainDelay;

    private String queueType;

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

        try {
            Config requestConfig = ConfigFactory.load().getConfig("spider.request");
            sleepTime = requestConfig.getInt("sleepTime");
            autoSleepTime = requestConfig.getBoolean("autoSleepTime");
            downloadDelay = requestConfig.getInt("downloadDelay");
            autoDownloadDelay = requestConfig.getBoolean("autoDownloadDelay");
            domainDelay = requestConfig.getInt("domainDelay");
            autoDomainDelay = requestConfig.getBoolean("autoDomainDelay");
        } catch (Exception e) {
            sleepTime = 0;
            autoSleepTime = true;
            downloadDelay = 0;
            autoDownloadDelay = true;
            domainDelay = 0;
            autoDomainDelay = true;
        }

        try {
            Config requestConfig = ConfigFactory.load().getConfig("spider.queue");
            queueType = requestConfig.getString("type");
        } catch (Exception e) {
            queueType = "default";
        }
    }

    public static final SpiderConfig getInsatance() {
        return SpiderConfig.Holder.instance;
    }

    private static class Holder {
        private static final SpiderConfig instance = new SpiderConfig();
    }
}
