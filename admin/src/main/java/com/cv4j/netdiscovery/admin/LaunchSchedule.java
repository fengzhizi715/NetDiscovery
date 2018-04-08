package com.cv4j.netdiscovery.admin;

import com.cv4j.netdiscovery.admin.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 服务器起来的时候，启动任务调度器，把数据库里配置的任务全部启动起来
 */
@Component
@Order(value=1)
@Slf4j
public class LaunchSchedule implements CommandLineRunner {

    @Autowired
    JobService jobService;

    @Override
    public void run(String... args) throws Exception {
        log.info("LaunchSchedule run");
        jobService.initSchedule();
    }
}
