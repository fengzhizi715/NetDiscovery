package com.cv4j.netdiscovery.admin.domain;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class JobModel {
    private Integer primaryId;
    private Integer jobConfigId;
    private String name;
    private String group;
    private String cron;
    private String remark;
    private String status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private JobConfigModel jobConfigModel;
}
