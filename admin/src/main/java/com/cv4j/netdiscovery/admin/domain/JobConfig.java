package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class JobConfig {

    private Integer primaryId;
    private String jobClassPath;
    private Class jobClazz;
    private String resourceName;
    private String cronExpression;
    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private String remark;
    private String state;
    private Timestamp createTime;
    private Timestamp updateTime;

}