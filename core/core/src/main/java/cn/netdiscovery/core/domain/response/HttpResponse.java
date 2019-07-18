package cn.netdiscovery.core.domain.response;

import lombok.Data;

/**
 * 对外暴露接口返回的 HttpResponse
 * Created by tony on 2018/1/26.
 */
@Data
public class HttpResponse<T> {

    private int code;

    private String message;

    private T data;
}
