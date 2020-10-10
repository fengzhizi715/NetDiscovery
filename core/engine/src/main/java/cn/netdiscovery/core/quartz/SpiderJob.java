package cn.netdiscovery.core.quartz;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * Created by tony on 2019-05-11.
 */
public class SpiderJob implements Job {

    private Logger log = LoggerFactory.getLogger(SpiderJob.class);

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
