package com.cv4j.netdiscovery.admin.job;

import com.cv4j.netdiscovery.admin.common.DateUtil;
import com.cv4j.netdiscovery.admin.domain.JobConfigModel;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class XicidailiJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobConfigModel jobConfigModel = new Gson().fromJson(jobDataMap.getString("jobConfig"), JobConfigModel.class);
        log.info("1 XicidailiJob run at " + DateUtil.getCurrentDateForTag() +", jobKey="+jobKey+", parserClass="+jobConfigModel.getParserClass());
        log.info("XicidailiJob startPage, endPage = "+jobConfigModel.getStartPage()+", "+jobConfigModel.getEndPage());
    }
}
