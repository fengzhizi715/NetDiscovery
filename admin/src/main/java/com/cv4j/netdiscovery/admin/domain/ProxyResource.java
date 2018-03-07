package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProxyResource {

    private String id;
    private Integer resId;
    private String webName;
    private String webUrl;
    private Integer pageCount;
    private String prefix;
    private String suffix;
    private String parser;
    private long addTime;
    private long modTime;
}
