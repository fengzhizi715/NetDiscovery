package com.cv4j.netdiscovery.core.http;

import com.safframework.tony.common.collection.NoEmptyHashMap;
import lombok.Getter;

import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
@Getter
public class Request {

    private String url;

    private String method;

    private String userAgent;

    private Map<String,String> header = new NoEmptyHashMap<>();

    private Map<String, String> cookies = new NoEmptyHashMap<>();

    public Request(String url) {

        this.url = url;
    }

    public Request ua(String userAgent) {

        this.userAgent = userAgent;
        return this;
    }

    public Request header(String name,String value) {

        header.put(name,value);
        return this;
    }

    public Request addCookie(String name, String value) {

        cookies.put(name, value);
        return this;
    }
}
