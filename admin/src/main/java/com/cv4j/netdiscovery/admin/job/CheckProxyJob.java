package com.cv4j.netdiscovery.admin.job;

import com.cv4j.netdiscovery.admin.service.ProxyService;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
@Slf4j
public class CheckProxyJob implements Job {

    @Autowired
    ProxyService proxyService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("CheckProxyJob start, size="+ProxyPool.proxyList.size());

        if(Preconditions.isNotBlank(ProxyPool.proxyList)) {
            for (int i = 0; i < ProxyPool.proxyList.size(); i++) {
                Proxy proxy = ProxyPool.proxyList.get(i);
                if(!proxyService.checkProxy(proxy)) {
                    ProxyPool.proxyList.remove(proxy);
                }
            }
        } else {
            log.info("CheckProxyJob is blank");
        }

        log.info("CheckProxyJob end, size="+ProxyPool.proxyList.size());
    }
}
