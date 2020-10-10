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
open class HttpResponse<T> {

    var code = 0
    var message: String? = null
    var data: T? = null

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

        @JvmField
        var Ok: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.Ok)

        @JvmField
        var Bad: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.BadRequest)

        @JvmField
        var NotFound: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.NotFound)

        @JvmField
        var InternalServerError: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.InternalServerError)

        @JvmField
        var SpiderNotFound: HttpResponse<*> = HttpResponse<Any?>(ResponseCode.SpiderNotFound)
    }
}