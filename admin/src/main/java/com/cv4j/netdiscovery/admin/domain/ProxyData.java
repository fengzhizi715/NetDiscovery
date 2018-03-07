package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProxyData {

    private String proxyType;
    private String proxyAddress;
    private int proxyPort;
    private long lastSuccessfulTime;

}
