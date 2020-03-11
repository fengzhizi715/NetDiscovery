package cn.netdiscovery.core.service.impl;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.bean.SpiderBean;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;
import cn.netdiscovery.core.service.RPCService;
import com.github.bdqfork.rpc.annotation.Service;
import com.safframework.tony.common.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @FileName: cn.netdiscovery.core.service.impl.RPCServiceImpl
 * @author: Tony Shen
 * @date: 2020-03-10 11:31
 * @version: V1.0 <描述当前版本功能>
 */
@Service(serviceInterface = RPCService.class)
public class RPCServiceImpl implements RPCService {

    private Map<String, Spider> spiders;
    private Map<String, SpiderJobBean> jobs;

    public RPCServiceImpl(Map<String, Spider> spiders,Map<String, SpiderJobBean> jobs) {
        this.spiders = spiders;
        this.jobs = jobs;
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

    @Override
    public List<SpiderBean> spiders() {
        List<SpiderBean> list = new ArrayList<>();

        Spider spider = null;
        SpiderBean entity = null;

        for (Map.Entry<String, Spider> entry : spiders.entrySet()) {

            spider = entry.getValue();

            entity = new SpiderBean();
            entity.setSpiderName(spider.getName());
            entity.setSpiderStatus(spider.getSpiderStatus());
            entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spider.getName()));
            entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spider.getName()));
            entity.setConsumedRequestSize(entity.getTotalRequestSize()-entity.getLeftRequestSize());
            entity.setQueueType(spider.getQueue().getClass().getSimpleName());
            entity.setDownloaderType(spider.getDownloader().getClass().getSimpleName());
            list.add(entity);
        }

        return list;
    }

    @Override
    public void status(String spiderName, int status) {
        if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName)!=null) {
            Spider spider = spiders.get(spiderName);

            switch (status) {
                case Spider.SPIDER_STATUS_PAUSE: {
                    spider.pause();
                    break;
                }
                case Spider.SPIDER_STATUS_RESUME: {
                    spider.resume();
                    break;
                }
                case Spider.SPIDER_STATUS_STOPPED: {
                    spider.forceStop();
                    break;
                }

                default:
                    break;
            }
        }
    }

    @Override
    public void push(String spiderName, String url) {
        if (Preconditions.isNotBlank(spiderName) && spiders.get(spiderName)!=null) {
            Spider spider = spiders.get(spiderName);
            spider.getQueue().pushToRunninSpider(url,spider);
        }
    }

    @Override
    public List<SpiderJobBean> jobs() {
        List<SpiderJobBean> list = new ArrayList<>();
        list.addAll(jobs.values());
        return list;
    }
}
