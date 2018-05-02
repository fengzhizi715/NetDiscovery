package com.cv4j.netdiscovery.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@MapperScan("com.cv4j.netdiscovery.admin.mapper")
public class LaunchAdmin {

    public static void main(String[] args) {
        SpringApplication.run(LaunchAdmin.class, args);
    }
}