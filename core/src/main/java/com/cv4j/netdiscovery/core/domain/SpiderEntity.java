package com.cv4j.netdiscovery.core.domain;

import lombok.Data;

/**
 * Created by tony on 2018/1/15.
 */
@Data
public class SpiderEntity {

    private String spiderName;
    private int spiderStatus;
    private int leftRequestSize;
    private int totalRequestSize;
    private String queueType;
}
