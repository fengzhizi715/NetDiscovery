package com.cv4j.netdiscovery.core.http;

import com.safframework.tony.common.collection.NoEmptyHashMap;
import lombok.Getter;

import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
public class Request {

    @Getter
    private String url;

    private String method;

    private Map<String,String> header = new NoEmptyHashMap<>();

    public Request(String url) {

        this.url = url;
    }

    public Request header(String name,String value) {

        header.put(name,value);
        return this;
    }
}
