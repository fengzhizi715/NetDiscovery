package com.cv4j.netdiscovery.core.domain.response;

import lombok.Data;

/**
 * Created by tony on 2018/1/26.
 */
@Data
public class HttpResponse<T> {

    private int code;

    private String message;

    private T data;
}
