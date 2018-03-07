package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourcePlan {

    private String id;
    private Integer resId;
    private Integer startPageNum;
    private Integer endPageNum;
    private long addTime;
    private long modTime;

}
