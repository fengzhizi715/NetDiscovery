package cn.netdiscovery.core.verticle;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;
import io.vertx.core.AbstractVerticle;

import java.util.Map;

/**
 * @FileName: cn.netdiscovery.core.verticle.AbstractRPCVerticle
 * @author: Tony Shen
 * @date: 2020-04-04 12:43
 * @version: V1.0 <描述当前版本功能>
 */
public class AbstractRPCVerticle extends AbstractVerticle {

    protected Map<String, Spider> spiders;
    protected Map<String, SpiderJobBean> jobs;

    public AbstractRPCVerticle(Map<String, Spider> spiders, Map<String, SpiderJobBean> jobs) {

        this.spiders = spiders;
        this.jobs = jobs;
    }
}
