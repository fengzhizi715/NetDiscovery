package com.cv4j.netdiscovery.admin.service;

import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.domain.SysOption;
import com.cv4j.netdiscovery.admin.dto.PageResult;

public interface ResourceService {

	boolean addResource(JobResource jobResource);

    boolean updateResource(JobResource jobResource);

	boolean deleteResource(Integer primaryId);

	PageResult<JobResource> getResourcesForList();

    JobResource getResourceByName(String resourceName);

    PageResult<SysOption> getOptionParsers();

    PageResult<SysOption> getOptionJobs();
}
