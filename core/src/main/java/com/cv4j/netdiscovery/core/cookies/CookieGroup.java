package com.cv4j.netdiscovery.core.cookies;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tony on 2018/2/1.
 */
@Data
public class CookieGroup {

    private String domain;
    private Map<String, String> cookies;

    public CookieGroup(String domain) {
        this.domain = domain;
        this.cookies = new HashMap<>();
    }
}
