package com.cv4j.netdiscovery.admin.handler;

import com.cv4j.vertx.web.aop.AopHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LogHandler implements AopHandler {

    Logger logger = LoggerFactory.getLogger(LogHandler.class);

    long startTime = 0;
    long endTime = 0;

    @Override
    public void before(Object... args) {
        startTime = System.currentTimeMillis();
        logger.info("before handler, startTime="+startTime);
    }

    @Override
    public void after(Object... args) {
        endTime = System.currentTimeMillis();
        logger.info("after handler, endTime="+endTime+", cost time="+(endTime-startTime));
    }
}
