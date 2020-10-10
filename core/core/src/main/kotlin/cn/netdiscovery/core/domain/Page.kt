package cn.netdiscovery.core.domain

import cn.netdiscovery.core.parser.selector.Html
import java.io.Serializable

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.Page
 * @author: Tony Shen
 * @date: 2020-10-10 15:24
 * @version: V1.0 <描述当前版本功能>
 */
class Page(
    var url: String? = null,
    var statusCode:Int = 0,//响应状态码
    var html: Html? = null, //response content
    var request: Request? = null,
    val resultItems:ResultItems = ResultItems()
) : Serializable {

    fun addRequest(request: Request?) {
        this.request = request
        resultItems.request = request
    }

    fun putField(key: String?, field: Any): Page {
        resultItems.put(key!!, field)
        return this
    }

    fun getField(key: String?): Any? {
        return resultItems.get<Any>(key!!)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val page = o as Page
        return url == page.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}