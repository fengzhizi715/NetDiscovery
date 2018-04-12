package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.domain.JobModel;
import com.cv4j.netdiscovery.admin.mapper.JobMapper;
import com.cv4j.netdiscovery.admin.service.JobService;
import com.google.gson.Gson;
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
    JobMapper jobMapper;

    @Autowired
    Scheduler scheduler;

    @Override
    public void initSchedule() throws SchedulerException {
        log.info("initSchedule start");
        List<JobModel> jobModelList = jobMapper.selectJobs();

        //把数据库里配置的job全部加入调度器
        for (JobModel jobModel : jobModelList) {
            jobModel.setJobConfigModel(jobMapper.selectJobConfig(jobModel.getJobConfigId()));
            addJob(jobModel);
        }

        // 启动调度器，开始工作
        if (!scheduler.isShutdown()) {
            log.info("scheduler start");
            scheduler.start();
        }
    }


    /**
     * 添加任务到调度器
     */
    public void addJob(JobModel jobModel) {
        log.info("addJob start, name="+jobModel.getName());
        try {
            // 创建JobDetail实例
            Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(jobModel.getJobConfigModel().getJobClass()).newInstance().getClass());
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobModel.getName(), jobModel.getGroup())
                    .usingJobData("jobConfig", new Gson().toJson(jobModel.getJobConfigModel()))  //传递参数给实际的job处理逻辑
                    .build();

            // 创建Trigger实例
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobModel.getCron());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobModel.getName(), jobModel.getGroup())  //和job用一样的name和group，jobdetail和trigger是不同类别
                    .withSchedule(scheduleBuilder)
                    .startNow()
                    .build();

            // 把作业JobDetail和触发器Trigger注册到调度器Scheduler中
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("addJob Exception");
        }
    }

    /**
     * 获取所有计划中的任务列表
     */
    public List<JobModel> getAllJobs() {
        log.info("getAllJobs start");
        List<JobModel> jobModelList = new ArrayList<>();
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    JobModel jobModel = new JobModel();
                    jobModel.setName(jobKey.getName());
                    jobModel.setGroup(jobKey.getGroup());
                    jobModel.setRemark("触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    jobModel.setStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        jobModel.setCron(cronTrigger.getCronExpression());
                    }
                    jobModelList.add(jobModel);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("getAllJobs SchedulerException");
        }
        return jobModelList;
    }

    /**
     * 所有正在运行的job
     */
    public List<JobModel> getRunningJob() {
        log.info("getRunningJob start");
        List<JobModel> jobModelList = new ArrayList<>();
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

            for (JobExecutionContext executingJob : executingJobs) {
                JobModel jobModel = new JobModel();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                jobModel.setName(jobKey.getName());
                jobModel.setGroup(jobKey.getGroup());
                jobModel.setRemark("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                jobModel.setStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    jobModel.setCron(cronTrigger.getCronExpression());
                }
                jobModelList.add(jobModel);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("getRunningJob SchedulerException");
        }
        return jobModelList;
    }

    /**
     * 暂停一个job
     */
    public void pauseJob(JobModel jobModel) {
        log.info("pauseJob start");
        try {
            JobKey jobKey = new JobKey(jobModel.getName(), jobModel.getGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null){
                return;
            }
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("pauseJob SchedulerException");
        }
    }

    /**
     * 恢复一个job
     */
    public void resumeJob(JobModel jobModel) {
        log.info("resumeJob start");
        try {
            JobKey jobKey = new JobKey(jobModel.getName(), jobModel.getGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null){
                return;
            }
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("resumeJob SchedulerException");
        }
    }

    /**
     * 删除一个job
     */
    public void deleteJob(JobModel jobModel) {
        log.info("deleteJob start");
        try {
            JobKey jobKey = new JobKey(jobModel.getName(), jobModel.getGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail==null){
                return;
            }
            TriggerKey triggerKey = new TriggerKey(jobModel.getName(), jobModel.getGroup());
            scheduler.pauseJob(jobKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("deleteJob SchedulerException");
        }
    }

    /**
     * 立即执行job
     */
    public void runJobNow(JobModel jobModel) {
        log.info("runJobNow start");
        try {
            JobKey jobKey = new JobKey(jobModel.getName(), jobModel.getGroup());
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("runJobNow SchedulerException");
        }
    }

    /**
     * 更新job时间表达式
     */
    public void updateJobCron(JobModel jobModel) {
        log.info("updateJobCron start");
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobModel.getName(), jobModel.getGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            log.info("updateJobCron TriggerState=" + scheduler.getTriggerState(triggerKey).toString());

            String oldCron = trigger.getCronExpression();
            log.info("updateJobCron oldCron=" + oldCron);
            if (!oldCron.equalsIgnoreCase(jobModel.getCron())) {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobModel.getCron());

                CronTrigger newTrigger = trigger.getTriggerBuilder()
                        .withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder)
                        .build();

                scheduler.rescheduleJob(triggerKey, newTrigger);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("updateJobCron SchedulerException");
        }
    }

    /**
     * 启动调度器
     */
    public void startScheduler(){
        log.info("startScheduler start");
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("startScheduler SchedulerException");
        }
    }

    /**
     * 关闭调度器
     */
    public void shutdownScheduler(){
        log.info("shutdownScheduler start");
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.info("shutdownScheduler SchedulerException");
        }
    }


}
