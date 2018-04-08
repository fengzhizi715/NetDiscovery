package com.cv4j.netdiscovery.admin.service;

import com.cv4j.netdiscovery.admin.domain.JobModel;
import org.quartz.SchedulerException;

public interface JobService {

    /**
     * 把数据库里配置的job全部纳入调度器运行起来，一般是在启动服务器的时候调用
     * @throws SchedulerException
     */
    void initSchedule() throws SchedulerException;

    void addJob(JobModel jobModel);



}
