package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class JobConfigModel {

    private Integer primaryId;
    private String jobName;
    private String jobClass;
    private String parserClass;
    private String urlPrefix;
    private String urlSuffix;
    private int startPage;
    private int endPage;
    private Timestamp createTime;
    private Timestamp updateTime;


}
