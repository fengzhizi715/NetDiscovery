package cn.netdiscovery.core.quartz;

import com.cv4j.proxy.ProxyManager;
import com.cv4j.proxy.ProxyPool;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by tony on 2019-05-16.
 */
public class ProxyPoolJob  implements Job {

    private Logger log = LoggerFactory.getLogger(SpiderJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("定时任务开始");

        log.info("jobName="+context.getJobDetail().getKey().getName());

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Map<String, Class> proxyMap = (Map<String, Class>) dataMap.get("proxyMap");

        if (proxyMap == null) return;

        ProxyPool.proxyMap = proxyMap;
        ProxyManager proxyManager = ProxyManager.get();
        proxyManager.start();
    }
}
