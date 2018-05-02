package com.cv4j.netdiscovery.admin.service;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import org.quartz.SchedulerException;

import java.util.List;

public interface JobService {

    /**
     * 启动调度器
     * @throws SchedulerException
     */
    boolean startScheduler();

    /**
     * 停止调度器
     * @throws SchedulerException
     */
    boolean stopScheduler();

    /**
     * 立即执行一个任务
     * @throws SchedulerException
     */
    boolean runJobNow(JobConfig jobConfig);

    /**
     * 添加一个任务到调度器
     */
    boolean addJob(JobConfig jobConfig);

    /**
     * 移除一个任务
     */
    boolean removeJob(JobConfig jobConfig);

    /**
     * 修改一个任务的执行时间
     */
    boolean modifyJobTime(JobConfig jobConfig);

    /**
     * 暂停一个任务
     */
    boolean pauseJob(JobConfig jobConfig);

    /**
     * 恢复一个任务
     */
    boolean resumeJob(JobConfig jobConfig);

    /**
     * 修改Job的state
     */
    boolean changeJobState(String jobName, String action);

    /**
     * 获取一个任务的当前状态
     */
    String getJobState(JobConfig jobConfig);

    /**
     * 获取调度器中所有的job
     * @return
     */
    PageResult<JobConfig> getAllJobs();

    /**
     * 获取调度器中正在执行的job
     * @return
     */
    PageResult<JobConfig> getRunningJobs();

}
