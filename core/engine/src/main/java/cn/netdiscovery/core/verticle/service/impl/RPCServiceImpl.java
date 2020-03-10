package cn.netdiscovery.core.verticle.service.impl;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.bean.SpiderBean;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;
import cn.netdiscovery.core.verticle.service.RPCService;
import com.github.bdqfork.rpc.annotation.Service;
import com.safframework.tony.common.utils.Preconditions;

import java.util.Map;

/**
 * @FileName: cn.netdiscovery.core.verticle.service.impl.RPCServiceImpl
 * @author: Tony Shen
 * @date: 2020-03-10 11:31
 * @version: V1.0 <描述当前版本功能>
 */
@Service(serviceInterface = RPCService.class)
public class RPCServiceImpl implements RPCService {

    private Map<String, Spider> spiders;
    private Map<String, SpiderJobBean> jobs;
    private boolean useMonitor;

    public RPCServiceImpl(Map<String, Spider> spiders,Map<String, SpiderJobBean> jobs,boolean useMonitor) {

        this.spiders = spiders;
        this.jobs = jobs;
        this.useMonitor = useMonitor;
    }

    @Override
    public SpiderBean detail(String spiderName) {

        if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName) != null) {
            Spider spider = spiders.get(spiderName);

            SpiderBean entity = new SpiderBean();
            entity.setSpiderName(spiderName);
            entity.setSpiderStatus(spider.getSpiderStatus());
            entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spiderName));
            entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spiderName));
            entity.setConsumedRequestSize(entity.getTotalRequestSize() - entity.getLeftRequestSize());
            entity.setQueueType(spider.getQueue().getClass().getSimpleName());
            entity.setDownloaderType(spider.getDownloader().getClass().getSimpleName());

            return entity;
        }

        return null;
    }
}
