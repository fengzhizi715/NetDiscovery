package com.cv4j.netdiscovery.admin.service;

import com.cv4j.netdiscovery.admin.dto.SpiderData;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.core.domain.SpiderEntity;

public interface SpiderService {

	PageResult<SpiderData> getSpiderDatas(SpiderEntity[] spiderEntities, Integer currentPageNo, Integer pageSize);

	PageResult<SpiderData> getSpiderDataByName(SpiderEntity spiderEntity);

}
