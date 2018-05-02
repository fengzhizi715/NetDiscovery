package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.mapper.JobConfigMapper;
import com.cv4j.netdiscovery.admin.service.JobService;
import com.cv4j.netdiscovery.admin.service.ProxyService;
import com.cv4j.proxy.domain.Proxy;
import com.cv4j.proxy.http.HttpManager;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProxyServiceImpl implements ProxyService {

    @Autowired
    JobService jobService;

    @Autowired
    JobConfigMapper jobConfigMapper;

    @Override
    public boolean runAllJobs() {
        log.info("runAllJobs start");
        jobService.startScheduler();

        boolean result = false;
        List<JobConfig> jobConfigList = jobConfigMapper.selectJobConfigs();
        for (int i = 0; i < jobConfigList.size(); i++) {
            JobConfig jobConfig = jobConfigList.get(i);
            result = jobService.addJob(jobConfig);
        }
        return result;
    }

    @Override
    public boolean checkProxy(Proxy proxy) {
        HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort(), proxy.getType());
        return HttpManager.get().checkProxy(httpHost);
    }
}