package com.cv4j.netdiscovery.admin;

import com.cv4j.netdiscovery.admin.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class StartJob implements CommandLineRunner {

    @Autowired
    ProxyService proxyService;

    @Override
    public void run(String... args) throws Exception {
        log.info("StartJob ....");
        proxyService.runAllJobs();
    }
}
