package com.cv4j.netdiscovery.admin.job;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.service.ResourceService;
import com.cv4j.proxy.ProxyManager;
import com.cv4j.proxy.ProxyPool;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
@Slf4j
public class CoderBusyJob implements Job {

    @Autowired
    ResourceService resourceService;

    ProxyManager proxyManager = ProxyManager.get();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("CoderBusyJob start");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobResource jobResource = resourceService.getResourceByName(jobDataMap.getString("resourceName"));

        ProxyPool.proxyMap = CommonUtil.getProxyPageMap(jobResource);
        if(Preconditions.isBlank(ProxyPool.proxyMap)) {
            log.info("ProxyPool.proxyMap is empty");
            return;
        } else {
            log.info("before proxyManager.start() size="+ProxyPool.proxyList.size());
            proxyManager.start();
            log.info("after proxyManager.start() size="+ProxyPool.proxyList.size());
        }
    }
}
