package cn.netdiscovery.core.domain.response

import cn.netdiscovery.core.constants.ResponseCode

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.response.HttpResponse
 * @author: Tony Shen
 * @date: 2020-10-10 16:37
 * @version: V1.0 <描述当前版本功能>
 */
class HttpResponse<T> {

    private var code = 0
    private var message: String? = null
    private var data: T? = null

    constructor() {}

    constructor(code: ResponseCode, message: String?) {
        this.code = code.code
        this.message = message
    }

    constructor(code: Int, message: String?) {
        this.code = code
        this.message = message
    }

    constructor(data: T) : this(ResponseCode.Ok) {
        this.data = data
    }

    constructor(code: ResponseCode) {
        this.code = code.code
        message = code.message
    }

    companion object {
        var Ok: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.Ok)
        var Bad: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.BadRequest)
        var NotFound: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.NotFound)
        var InternalServerError: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.InternalServerError)
        var SpiderNotFound: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.SpiderNotFound)
    }
}