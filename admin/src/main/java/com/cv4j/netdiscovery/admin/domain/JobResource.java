package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class JobResource {

    private Integer primaryId;
    private String resourceName;
    private String parserClassPath;
    private String urlPrefix;
    private String urlSuffix;
    private int startPage;
    private int endPage;
    private Timestamp createTime;
    private Timestamp updateTime;

}