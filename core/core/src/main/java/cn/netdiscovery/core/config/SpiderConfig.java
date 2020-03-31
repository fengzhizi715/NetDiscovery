package cn.netdiscovery.core.config;

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
    private long initialDelay;
    private int maxRetries;
    private long retryDelayMillis;
    private boolean usePrintRequestPipeline;
    private boolean useConsolePipeline;

    private long sleepTime;
    private boolean autoSleepTime;
    private long  downloadDelay;
    private boolean autoDownloadDelay;
    private long domainDelay;
    private boolean autoDomainDelay;

    private String queueType;

    private long pipelineDelay;
    private boolean autoPipelineDelay;

    private String downloaderType;
    private boolean keepAlive;
    private boolean reuseAddress;
    private boolean followRedirects;
    private int connectTimeout;
    private int idleTimeout;
    private int maxWaitQueueSize;

    private SpiderConfig() {

        try {
            Config config = ConfigFactory.load().getConfig("spider.config");
            autoProxy = config.getBoolean("autoProxy");
            initialDelay = config.getLong("initialDelay");
            maxRetries = config.getInt("maxRetries");
            retryDelayMillis = config.getLong("retryDelayMillis");
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
            sleepTime = requestConfig.getLong("sleepTime");
            autoSleepTime = requestConfig.getBoolean("autoSleepTime");
            downloadDelay = requestConfig.getLong("downloadDelay");
            autoDownloadDelay = requestConfig.getBoolean("autoDownloadDelay");
            domainDelay = requestConfig.getLong("domainDelay");
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
            Config queueConfig = ConfigFactory.load().getConfig("spider.queue");
            queueType = queueConfig.getString("type");
        } catch (Exception e) {
            queueType = "default";
        }

        try {
            Config pipelineConfig = ConfigFactory.load().getConfig("spider.pipeline");
            pipelineDelay = pipelineConfig.getLong("pipelineDelay");
            autoPipelineDelay = pipelineConfig.getBoolean("autoPipelineDelay");
        } catch (Exception e) {
            pipelineDelay = 0;
            autoPipelineDelay = false;
        }

        try {
            Config downloaderConfig = ConfigFactory.load().getConfig("spider.downloader");
            downloaderType = downloaderConfig.getString("type");
            Config optionConfig = downloaderConfig.getConfig("vertx.options");
            keepAlive = optionConfig.getBoolean("keepAlive");
            reuseAddress = optionConfig.getBoolean("reuseAddress");
            followRedirects = optionConfig.getBoolean("followRedirects");
            connectTimeout = optionConfig.getInt("connectTimeout");
            idleTimeout = optionConfig.getInt("idleTimeout");
            maxWaitQueueSize = optionConfig.getInt("maxWaitQueueSize");
        } catch (Exception e) {
            keepAlive = true;
            reuseAddress = true;
            followRedirects = true;
            connectTimeout = 10000;
            idleTimeout = 10;
            maxWaitQueueSize = 10;
        }
    }

    public static final SpiderConfig getInstance() {
        return SpiderConfig.Holder.instance;
    }

    private static class Holder {
        private static final SpiderConfig instance = new SpiderConfig();
    }
}
