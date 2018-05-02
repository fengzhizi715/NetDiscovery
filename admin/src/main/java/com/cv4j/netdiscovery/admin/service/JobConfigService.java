package com.cv4j.netdiscovery.admin.service;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.dto.PageResult;

public interface JobConfigService {

	boolean addJobConfig(JobConfig jobConfig);

    boolean updateJobConfig(JobConfig jobConfig);

	boolean deleteJobConfig(Integer primaryId);

	JobConfig getJobByName(String jobName);

	PageResult<JobConfig> getJobConfigsForList();

}
