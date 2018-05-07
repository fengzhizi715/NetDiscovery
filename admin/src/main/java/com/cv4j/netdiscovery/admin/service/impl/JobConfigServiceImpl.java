package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.mapper.JobConfigMapper;
import com.cv4j.netdiscovery.admin.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.utils.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class JobConfigServiceImpl implements JobConfigService {
    @Autowired
    private JobConfigMapper jobConfigMapper;

    @Override
    public boolean addJobConfig(JobConfig jobConfig) {
        jobConfig.setJobGroupName(Key.DEFAULT_GROUP);  //TODO 暂时jobName做唯一标识就足够了
        jobConfig.setTriggerName(jobConfig.getJobName());
        jobConfig.setTriggerGroupName(Key.DEFAULT_GROUP);
        jobConfig.setCreateTime(new Timestamp(new Date().getTime()));
        jobConfig.setUpdateTime(new Timestamp(new Date().getTime()));
        return jobConfigMapper.insertJobConfig(jobConfig)>0;
    }

    @Override
    public boolean updateJobConfig(JobConfig jobConfig) {
        jobConfig.setUpdateTime(new Timestamp(new Date().getTime()));
        return jobConfigMapper.updateJobConfigByPrimaryKey(jobConfig)>0;
    }

    @Override
    public boolean deleteJobConfig(Integer primaryId) {
        return jobConfigMapper.deleteJobConfigByPrimaryKey(primaryId)>0;
    }

    @Override
    public JobConfig getJobByName(String jobName) {
        return jobConfigMapper.selectJobByName(jobName).get(0);
    }

    @Override
    public PageResult<JobConfig> getJobConfigsForList() {
        List<JobConfig> jobConfigList = jobConfigMapper.selectJobConfigs();
        return new PageResult<>(jobConfigList, jobConfigList.size());
    }

}