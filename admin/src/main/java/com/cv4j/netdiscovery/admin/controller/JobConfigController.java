package com.cv4j.netdiscovery.admin.controller;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.dto.ResultMap;
import com.cv4j.netdiscovery.admin.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/jobconfig")
@Slf4j
public class JobConfigController {

    @Autowired
    private JobConfigService jobConfigService;

    @PostMapping("/save")
    public ResultMap saveJobConfig(@RequestBody JobConfig jobConfig) {
        if(!CronExpression.isValidExpression(jobConfig.getCronExpression())) {
            return ResultMap.error("Cron表达式无效");
        }

        boolean result = false;
        if(jobConfig.getPrimaryId() == null ) {
            result = jobConfigService.addJobConfig(jobConfig);
        } else {
            result = jobConfigService.updateJobConfig(jobConfig);
        }

        return result ?  ResultMap.ok() : ResultMap.error();
    }

    @DeleteMapping("/delete/{id}")
    public ResultMap deleteResource(@PathVariable("id") Integer id) {
        if(jobConfigService.deleteJobConfig(id)){
            return ResultMap.deleteSuccess();
        }else{
            return ResultMap.deleteFailure();
        }
    }

    @GetMapping("/list")
    public PageResult<JobConfig> listJobConfigs() {
        return jobConfigService.getJobConfigsForList();
    }

}