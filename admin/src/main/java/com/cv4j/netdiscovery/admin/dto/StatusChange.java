package com.cv4j.netdiscovery.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusChange {
    private String engineUrl;
    private String spiderName;
    private Integer toStatus;
}
