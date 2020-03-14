package cn.netdiscovery.core.constants;

import cn.netdiscovery.core.domain.response.HttpResponse;

/**
 * Created by tony on 2019-07-19.
 */
public enum ResponseCode {

    Ok(0, "ok"),
    BadRequest(400, "请求参数有误"),
    NotFound(404, "数据不存在"),
    InternalServerError(500, "网络异常，请稍后再试"),
    SpiderNotFound(1001,"爬虫不在容器中");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpResponse toResponse() {
        return toResponse(message);
    }

    public HttpResponse toResponse(String message) {
        return new HttpResponse(this.code, message);
    }
}
