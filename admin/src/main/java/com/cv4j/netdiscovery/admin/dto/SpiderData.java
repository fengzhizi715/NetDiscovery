package com.cv4j.netdiscovery.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpiderData {
    private String spiderName;
    private Integer spiderStatus;
    private Integer remainCount;
    private Integer finishCount;
}
