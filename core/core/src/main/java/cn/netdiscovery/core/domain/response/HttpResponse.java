package cn.netdiscovery.core.domain.response;

import lombok.Data;

/**
 * 返回的HttpResponse
 * Created by tony on 2018/1/26.
 */
@Data
public class HttpResponse<T> {

    private int code;

    private String message;

    private T data;
}
