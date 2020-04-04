package cn.netdiscovery.core.quartz;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.stream.Stream;

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
        Request[] requests = (Request[]) dataMap.get("requests");

        if (spider!=null && Preconditions.isNotBlank(requests)) {

            log.info("spiderName="+spider.getName());

            if (spider.getSpiderStatus() == Spider.SPIDER_STATUS_INIT
                    || spider.getSpiderStatus() == Spider.SPIDER_STATUS_STOPPED) {

                spider.run();
            } else if (spider.getSpiderStatus() == Spider.SPIDER_STATUS_PAUSE) {

                spider.resume();
            }

            Stream.of(requests).forEach(request -> spider.getQueue().pushToRunninSpider(request,spider));
        }
    }
}
