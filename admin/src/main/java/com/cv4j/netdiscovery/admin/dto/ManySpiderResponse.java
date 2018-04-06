package com.cv4j.netdiscovery.admin.dto;

import com.cv4j.netdiscovery.core.domain.SpiderEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManySpiderResponse {
    private int code;

    private String message;

    private SpiderEntity[] data;
}
