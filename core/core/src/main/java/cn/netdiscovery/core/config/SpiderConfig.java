package cn.netdiscovery.core.config;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.context.configuration.Value;

/**
 * @FileName: cn.netdiscovery.core.config.SpiderConfig
 * @author: Tony Shen
 * @date: 2020-02-22 15:44
 * @version: V1.0 <描述当前版本功能>
 */
@Configuration(location = "application.yaml")
public class SpiderConfig {

    @Value("spider.config.autoProxy")
    private boolean autoProxy;

    @Value("spider.config.initialDelay")
    private int initialDelay;

    public boolean isAutoProxy() {
        return autoProxy;
    }

    public void setAutoProxy(boolean autoProxy) {
        this.autoProxy = autoProxy;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }
}
