package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @Autowired
    Scheduler scheduler;

    @Override
    public boolean startScheduler() {
        try {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean stopScheduler() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean runJobNow(JobConfig jobConfig) {
        try {
            JobKey jobKey = new JobKey(jobConfig.getJobName(), jobConfig.getJobGroupName());
            scheduler.triggerJob(jobKey);
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addJob(JobConfig jobConfig) {
        try {
            // 从调度器中获取指定群组的指定名称的触发器实例；
            TriggerKey triggerKey = TriggerKey.triggerKey(jobConfig.getTriggerName(), jobConfig.getTriggerGroupName());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                //调度器里存在该job，那就进行更新（移除、添加）
                return modifyJobTime(jobConfig);
            } else {
                try {
                    // 创建JobDetail实例
                    Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(jobConfig.getJobClassPath()).newInstance().getClass());
                    JobDetail jobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity(jobConfig.getJobName(), jobConfig.getJobGroupName())
                            .usingJobData("resourceName", jobConfig.getResourceName())
                            .build();

                    // 创建Trigger实例
                    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobConfig.getCronExpression());
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(jobConfig.getTriggerName(), jobConfig.getTriggerGroupName())  //和JobDetail用一样的name和group，jobdetail和trigger是不同类别，所以可以重复name
                            .withSchedule(scheduleBuilder)
                            .startNow()
                            .build();

                    // 把作业JobDetail和触发器Trigger注册到调度器Scheduler中
                    scheduler.scheduleJob(jobDetail, trigger);
                    return true;
                } catch(Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeJob(JobConfig jobConfig) {
        try {
            JobKey jobKey = new JobKey(jobConfig.getJobName(), jobConfig.getJobGroupName());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail==null){
                return false;
            }

            TriggerKey triggerKey = new TriggerKey(jobConfig.getTriggerName(), jobConfig.getTriggerGroupName());

            // 暂停任务；
            scheduler.pauseJob(jobKey);
            // 取消任务调度；
            scheduler.unscheduleJob(triggerKey);
            // 删除任务；
            scheduler.deleteJob(jobKey);
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean pauseJob(JobConfig jobConfig) {
        try {
            JobKey jobKey = new JobKey(jobConfig.getJobName());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null){
                return false;
            }

            scheduler.pauseJob(jobKey);
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean resumeJob(JobConfig jobConfig) {
        try {
            JobKey jobKey = new JobKey(jobConfig.getJobName(), jobConfig.getJobGroupName());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null){
                return false;
            }

            scheduler.resumeJob(jobKey);
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeJobState(String jobName, String action) {
        try {
            JobKey jobKey = new JobKey(jobName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null){
                return false;
            }

            if("pause".equals(action)) {
                scheduler.pauseJob(jobKey);
            } else if("resume".equals(action)) {
                scheduler.resumeJob(jobKey);
            } else {
                return false;
            }
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean modifyJobTime(JobConfig jobConfig) {
        try {
            // 从调度器中获取指定群组的指定名称的触发器实例；
            TriggerKey triggerKey = TriggerKey.triggerKey(jobConfig.getTriggerName(), jobConfig.getTriggerGroupName());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 对获取的触发器进行判空操作；
            if (trigger == null) {
                return false;
            }
            String oldCronExpression = trigger.getCronExpression();
            // 如果新的执行时间与旧的执行时间相同，则返回，不继续下一步的操作；
            if (oldCronExpression.equalsIgnoreCase(jobConfig.getCronExpression())) {
                return false;
            }
            // 移除旧的任务
            this.removeJob(jobConfig);
            // 添加新的任务
            this.addJob(jobConfig);
            return true;
        } catch(SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * STATE_BLOCKED 	4  后面有排着队等着的job
     * STATE_ERROR 	    3
     * STATE_COMPLETE 	2
     * STATE_PAUSED 	1  暂停
     * STATE_NORMAL 	0  正常
     * STATE_NONE 	   -1  （没有了依赖）
     *
     * 1、2、3、4、5
     */
    @Override
    public String getJobState(JobConfig jobConfig) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobConfig.getTriggerName(), jobConfig.getTriggerGroupName());
            return scheduler.getTriggerState(triggerKey).name();
        } catch(SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageResult<JobConfig> getAllJobs() {
        List<JobConfig> jobConfigList = new ArrayList<>();
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    TriggerKey triggerKey = trigger.getKey();
                    JobConfig jobConfig = new JobConfig();
                    jobConfig.setJobName(jobKey.getName());
                    jobConfig.setJobGroupName(jobKey.getGroup());
                    jobConfig.setTriggerName(triggerKey.getName());
                    jobConfig.setTriggerGroupName(triggerKey.getGroup());

                    Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                    jobConfig.setState(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        jobConfig.setCronExpression(cronTrigger.getCronExpression());
                    }
                    jobConfigList.add(jobConfig);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("getAllJobs SchedulerException");
        }

        return new PageResult<>(jobConfigList, jobConfigList.size());
    }

    @Override
    public PageResult<JobConfig> getRunningJobs() {
        List<JobConfig> jobConfigList = new ArrayList<>();
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext executingJob : executingJobs) {
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                TriggerKey triggerKey = trigger.getKey();
                JobConfig jobConfig = new JobConfig();
                jobConfig.setJobName(jobKey.getName());
                jobConfig.setJobGroupName(jobKey.getGroup());
                jobConfig.setTriggerName(triggerKey.getName());//和job用同样的名字
                jobConfig.setTriggerGroupName(triggerKey.getGroup());

                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                jobConfig.setState(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    jobConfig.setCronExpression(cronTrigger.getCronExpression());
                }
                jobConfigList.add(jobConfig);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("getRunningJob SchedulerException");
        }
        return new PageResult<>(jobConfigList, jobConfigList.size());
    }

    /**
     * 添加任务到调度器(简单trigger)
     */
    private void addJobWithSimpleTrigger(JobConfig jobConfig) {
        log.info("addJob start, name="+ jobConfig.getJobName());
        try {
            // 创建JobDetail实例
            Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(jobConfig.getJobClassPath()).newInstance().getClass());
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobConfig.getJobName(), jobConfig.getJobGroupName())
                    //                    .usingJobData("dataMap", jobConfig.getParamMap())
                    .build();

            // 创建Trigger实例
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobConfig.getJobName(), jobConfig.getJobGroupName())
                    .withSchedule(scheduleBuilder)
                    .startNow()
                    .build();

            // 把作业JobDetail和触发器Trigger注册到调度器Scheduler中
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("addJobForSimpleTrigger Exception");
        }
    }

}