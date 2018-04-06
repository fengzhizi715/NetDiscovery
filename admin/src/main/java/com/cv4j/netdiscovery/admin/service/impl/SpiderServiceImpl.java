package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.dto.SpiderData;
import com.cv4j.netdiscovery.admin.dto.PageResult;
import com.cv4j.netdiscovery.admin.service.SpiderService;
import com.cv4j.netdiscovery.core.domain.SpiderEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpiderServiceImpl implements SpiderService {

	@Override
	public PageResult<SpiderData> getSpiderDatas(SpiderEntity[] spiderEntities, Integer currentPageNo, Integer pageSize) {
		//总记录数
		int totalCount = spiderEntities.length;
		//当前页的第一个索引
		int startIndex = (currentPageNo - 1) * pageSize;
		//从当前页第一个索引开始，计算剩余的记录数
		int remainCount = totalCount - startIndex;
		//如果剩余的记录数还超过limit，那当前页还显示limit数量，不然就显示剩下的数量
		int endIndex = remainCount > pageSize ? startIndex+pageSize : startIndex+remainCount;

		List<SpiderData> spiderDataList = new ArrayList<>();
		for (int i = startIndex; i < endIndex; i++) { //endIndex不计算在当前页
			spiderDataList.add(coventToSpiderData(spiderEntities[i]));
		}

		return new PageResult<SpiderData>(spiderDataList, totalCount);
	}

	@Override
	public PageResult<SpiderData> getSpiderDataByName(SpiderEntity spiderEntity) {
		List<SpiderData> spiderDataList = new ArrayList<>();
		spiderDataList.add(coventToSpiderData(spiderEntity));

		return new PageResult<SpiderData>(spiderDataList, spiderDataList.size());
	}

	private SpiderData coventToSpiderData(SpiderEntity spiderEntity) {
		SpiderData spiderData = new SpiderData();
		spiderData.setSpiderName(spiderEntity.getSpiderName());
		spiderData.setSpiderStatus(spiderEntity.getSpiderStatus());
		spiderData.setRemainCount(spiderEntity.getLeftRequestSize());
		spiderData.setFinishCount(spiderEntity.getTotalRequestSize() - spiderEntity.getLeftRequestSize());
		return spiderData;
	}
}