package cn.netdiscovery.core.domain

import java.io.InputStream
import java.io.Serializable

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.Response
 * @author: Tony Shen
 * @date: 2020-10-10 16:06
 * @version: V1.0 对各个网络框架返回的 response 的封装
 */
class Response : Serializable {

    var content: ByteArray?=null
    var statusCode:Int = 0
    var contentType: String? = null
    var `is`: InputStream? = null
}