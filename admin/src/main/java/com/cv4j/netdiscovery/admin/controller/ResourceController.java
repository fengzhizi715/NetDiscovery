package com.cv4j.netdiscovery.admin.controller;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.domain.SysOption;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.dto.ResultMap;
import com.cv4j.netdiscovery.admin.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/resource")
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/save")
    public ResultMap saveResource(@RequestBody JobResource jobResource) {
        boolean result = false;
        if(jobResource.getPrimaryId() == null ) {
            result = resourceService.addResource(jobResource);
        } else {
            result = resourceService.updateResource(jobResource);
        }

        return result ?  ResultMap.ok() : ResultMap.error();
    }

    @DeleteMapping("/delete/{id}")
    public ResultMap deleteResource(@PathVariable("id") Integer id) {
        if(resourceService.deleteResource(id)){
            return ResultMap.deleteSuccess();
        }else{
            return ResultMap.deleteFailure();
        }
    }

    @GetMapping("/list")
    public PageResult<JobResource> listResources() {
        return resourceService.getResourcesForList();
    }

    @GetMapping("/{resourceName}")
    public ResultMap checkResourceByName(@PathVariable String resourceName) {
        JobResource jobResource = resourceService.getResourceByName(resourceName);
        if(jobResource != null){
            return ResultMap.ok("系统已经存在 "+resourceName);
        }else{
            return ResultMap.error();
        }
    }

    @GetMapping("/options/{optType}")
    public PageResult<SysOption> loadOptions(@PathVariable String optType) {
        PageResult<SysOption> resultList = null;
        if("parserClass".equalsIgnoreCase(optType)) {//解析类
            resultList = resourceService.getOptionParsers();
        } else if("jobClass".equalsIgnoreCase(optType)) {//job类
            resultList = resourceService.getOptionJobs();
        } else {
            log.info("optType error");
        }

        return resultList;
    }

}