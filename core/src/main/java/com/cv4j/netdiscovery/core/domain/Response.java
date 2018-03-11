package com.cv4j.netdiscovery.core.domain;

import lombok.Data;

import java.io.InputStream;

/**
 * Created by tony on 2018/1/20.
 */
@Data
public class Response {

    private byte[] content;
    private int statusCode;
    private String contentType;
    private InputStream is;
}
