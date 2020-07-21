package cn.netdiscovery.core.domain.response;

import cn.netdiscovery.core.constants.ResponseCode;
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

    public static HttpResponse Ok = new HttpResponse(ResponseCode.Ok);
    public static HttpResponse Bad = new HttpResponse(ResponseCode.BadRequest);
    public static HttpResponse NotFound = new HttpResponse(ResponseCode.NotFound);
    public static HttpResponse InternalServerError = new HttpResponse(ResponseCode.InternalServerError);
    public static HttpResponse SpiderNotFound = new HttpResponse(ResponseCode.SpiderNotFound);

    public HttpResponse() {
    }

    public HttpResponse(ResponseCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    public HttpResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResponse(T data) {
        this(ResponseCode.Ok);
        this.data = data;
    }

    public HttpResponse(ResponseCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }
}
