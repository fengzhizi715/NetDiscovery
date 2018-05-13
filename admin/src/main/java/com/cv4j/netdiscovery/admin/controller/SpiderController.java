package com.cv4j.netdiscovery.admin.controller;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import com.cv4j.netdiscovery.admin.config.Constant;
import com.cv4j.netdiscovery.admin.dto.*;
import com.cv4j.netdiscovery.admin.service.SpiderService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value="/spider")
@Slf4j
public class SpiderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SpiderService spiderService;

    /**
     * 从爬虫引擎提供的api中获取爬虫的信息
     */
    @PostMapping("/list")
    public PageResult<SpiderData> listSpiders(String engineUrl, String spiderName, Integer page, Integer limit) {
        log.info("spiderName="+spiderName+",engineUrl="+engineUrl);
        String url = "";
        if("all".equals(spiderName)) {
            //查全部
            url = engineUrl+ Constant.API_SPIDERS;
            log.debug("url="+url);
            try {
                ManySpiderResponse manySpiderResponse = restTemplate.getForObject(url, ManySpiderResponse.class);
                return spiderService.getSpiderDatas(manySpiderResponse.getData(), page, limit);
            } catch(Exception e) {
                log.info(e.getMessage());
                return new PageResult(500);
            }
        } else {
            //查一个
            url = engineUrl+ Constant.API_SPIDER_BY_NAME.replaceAll("spiderName", spiderName.toUpperCase());
            log.debug("url="+url);
            try {
                OneSpiderResponse oneSpiderResponse = restTemplate.getForObject(url, OneSpiderResponse.class);
                return spiderService.getSpiderDataByName(oneSpiderResponse.getData());
            } catch(Exception e) {
                log.info(e.getMessage());
                return new PageResult(500);
            }
        }
    }

    /**
     * 改变某一个爬虫的状态
     */
    @PostMapping("/status")
    public ResultMap changeSpiderStatus(@RequestBody StatusChange statusChange) {
        log.info("spiderName="+statusChange.getSpiderName()+",toStatus="+statusChange.getToStatus());
        String url = statusChange.getEngineUrl()+ Constant.API_SPIDER_STATUS.replaceAll("spiderName", statusChange.getSpiderName().toUpperCase());
        log.debug("url="+url);

        JsonObject postBody = new JsonObject();
        postBody.addProperty("status", statusChange.getToStatus());

        try {
            PostResponse postResponse = restTemplate.postForObject(url, CommonUtil.getHttpEntityForRequest(postBody), PostResponse.class);
            return postResponse.getCode() == 200 ? ResultMap.ok() : ResultMap.error();
        } catch(Exception e) {
            log.info(e.getMessage());
            return ResultMap.error();
        }
    }

}