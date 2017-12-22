package com.cv4j.netdiscovery.core.http;

import lombok.Getter;

/**
 * Created by tony on 2017/12/23.
 */
public class Request {

    @Getter
    private String url;

    private String method;

    public Request(String url) {

        this.url = url;
    }
}
