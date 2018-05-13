package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import com.cv4j.netdiscovery.admin.config.Constant;
import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.domain.SysOption;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.mapper.ResourceMapper;
import com.cv4j.netdiscovery.admin.service.ResourceService;
import com.cv4j.proxy.ProxyListPageParser;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ResourceServiceImpl implements ResourceService {

	@Autowired
	private ResourceMapper resourceMapper;

	@Override
	public boolean addResource(JobResource jobResource) {
		jobResource.setCreateTime(new Timestamp(new Date().getTime()));
		jobResource.setUpdateTime(new Timestamp(new Date().getTime()));
		return resourceMapper.insertResource(jobResource)>0;
	}

	@Override
	public boolean updateResource(JobResource jobResource) {
		jobResource.setUpdateTime(new Timestamp(new Date().getTime()));
		return resourceMapper.updateResourceByPrimaryKey(jobResource)>0;
	}

	@Override
	public boolean deleteResource(Integer primaryId) {
		return resourceMapper.deleteResourceByPrimaryKey(primaryId)>0;
	}

	@Override
	public PageResult<JobResource> getResourcesForList() {
		List<JobResource> jobResourceList = resourceMapper.selectResources();
		return new PageResult<>(jobResourceList, jobResourceList.size());
	}

	@Override
	public JobResource getResourceByName(String resourceName) {
		return resourceMapper.selectOneResource(resourceName);
	}

	@Override
	public PageResult<SysOption> getOptionParsers() {
//		List<SysOption> resultList = resourceMapper.selectParsers();
		//改成从package获取
		List<SysOption> resultList = new ArrayList<>();

		//把指定package下实现了某个接口的类找出来
		Set<Class<?>> allClasses = CommonUtil.getClassesFromPackage(Constant.PACKAGE_PARSER);
		Set<Class<?>> classes = CommonUtil.getClassesByInterface(ProxyListPageParser.class, allClasses);
		Iterator iterator = classes.iterator();
		while (iterator.hasNext()) {
			String classPath = iterator.next().toString();
			SysOption sysOption = new SysOption();
			sysOption.setOptText(classPath.substring(classPath.lastIndexOf(".") + 1));
			System.out.println("classPath="+classPath);
			sysOption.setOptValue(classPath.split(" ")[1]);
			resultList.add(sysOption);
		}

		return new PageResult<>(resultList, resultList.size());
	}

	@Override
	public PageResult<SysOption> getOptionJobs() {
//		List<SysOption> resultList = resourceMapper.selectJobs();
		//改成从package获取
		List<SysOption> resultList = new ArrayList<>();
		Set<Class<?>> allClasses = CommonUtil.getClassesFromPackage(Constant.PACKAGE_JOB);
		Set<Class<?>> classes = CommonUtil.getClassesByInterface(Job.class, allClasses);
		Iterator iterator = classes.iterator();
		while (iterator.hasNext()) {
			String classPath = iterator.next().toString();
			SysOption sysOption = new SysOption();
			sysOption.setOptText(classPath.substring(classPath.lastIndexOf(".") + 1));
			sysOption.setOptValue(classPath.split(" ")[1]);
			resultList.add(sysOption);
		}

		return new PageResult<>(resultList, resultList.size());
	}
}