package com.cv4j.netdiscovery.admin.controller;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.dto.ResultMap;
import com.cv4j.netdiscovery.admin.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(value="/job")
@Slf4j
public class JobController {

    @Autowired
    private JobService jobService;

    //任务的添加、移除
    @PostMapping("/add")
    public ResultMap addJob(@RequestBody JobConfig jobConfig) {
        jobService.startScheduler();
        return jobService.addJob(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    //启动一个任务
    @PostMapping("/run")
    public ResultMap runJob(@RequestBody JobConfig jobConfig) {
        jobService.startScheduler();
        return jobService.runJobNow(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    //任务的暂停、继续
    @PostMapping("/pause")
    public ResultMap pauseJob(@RequestBody JobConfig jobConfig) {
        return jobService.pauseJob(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    @PostMapping("/resume")
    public ResultMap resumeJob(@RequestBody JobConfig jobConfig) {
        return jobService.resumeJob(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    @GetMapping("/{jobName}/{action}")
    public ResultMap changeJobState(@PathVariable String jobName, @PathVariable String action) {
        return jobService.changeJobState(jobName, action) ? ResultMap.ok() : ResultMap.error();
    }

    @PostMapping("/remove")
    public ResultMap removeJob(@RequestBody JobConfig jobConfig) {
        return jobService.removeJob(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    //获取所有的任务、 运行中的任务
    @GetMapping("/list/{type}")
    public PageResult<JobConfig> getJobList(@PathVariable String type) {
        if("all".equals(type)) {
            return jobService.getAllJobs();
        } else if("run".equals(type)) {
            return jobService.getRunningJobs();
        } else {
            return new PageResult(500);
        }
    }

    //更新任务
    @PostMapping("/modifytime")
    public ResultMap modifyJobTime(@RequestBody JobConfig jobConfig) {
        //TODO 要不要更新数据库？
        return jobService.modifyJobTime(jobConfig) ? ResultMap.ok() : ResultMap.error();
    }

    //获取任务的当前状态
    @PostMapping("/state")
    public ResultMap getJobState(@RequestBody JobConfig jobConfig) {
        String state = jobService.getJobState(jobConfig);
        return ResultMap.ok().put("state", state);
    }

    //调度器的开、关
    @GetMapping("/scheduler/{action}")
    public ResultMap schedulerAction(@PathVariable String action) {
        if("start".equals(action)) {
            return jobService.startScheduler() ? ResultMap.ok() : ResultMap.error();
        } else if("stop".equals(action)) {
            return jobService.stopScheduler() ? ResultMap.ok() : ResultMap.error();
        } else {
            return ResultMap.error();
        }
    }

}