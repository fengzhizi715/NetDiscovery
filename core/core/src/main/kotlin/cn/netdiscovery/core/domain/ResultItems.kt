package cn.netdiscovery.core.domain

import java.io.Serializable
import java.util.LinkedHashMap

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.ResultItems
 * @author: Tony Shen
 * @date: 2020-10-10 15:02
 * @version: V1.0 <描述当前版本功能>
 */
class ResultItems (var request: Request? = null,
                   val skip:Boolean = false // 判断结果是否需要被 Pipeline 处理
                  ): Serializable {

    val fields: MutableMap<String, Any?> = LinkedHashMap()

    fun <T> get(key: String): T? {
        return if (fields[key] != null) fields[key] as T? else null
    }

    fun getAll(): Map<String, Any?> {
        return fields
    }

    fun <T> put(key: String, value: T): ResultItems {
        fields[key] = value
        return this
    }
}