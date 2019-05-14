package cn.netdiscovery.core.quartz;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by tony on 2019-05-11.
 */
@Slf4j
public class SpiderJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("定时任务开始");

        log.info("jobName="+context.getJobDetail().getKey().getName());

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Spider spider = (Spider) dataMap.get("spider");
        Request request = (Request) dataMap.get("request");

        if (spider!=null && request!=null) {

            log.info("spiderName="+spider.getName());
            log.info("request="+request.toString());

            spider.getQueue().pushToRunninSpider(request,spider);
        }
    }
}
