package cn.netdiscovery.core.domain

import com.google.gson.JsonObject
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicNameValuePair
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.core.domain.HttpRequestBody
 * @author: Tony Shen
 * @date: 2020-10-10 16:11
 * @version: V1.0 <描述当前版本功能>
 */
class HttpRequestBody(body: ByteArray?, contentType: String?, encoding: String?) :
    Serializable {

    var body: ByteArray?

    var contentType: String?

    var encoding: String?

    init {
        this.body = body
        this.contentType = contentType
        this.encoding = encoding
    }

    companion object {

        /**
         * post body 使用 jsonObject 时，传递一个 jsonObject 即可生成 HttpRequestBody 对象
         * @param jsonObject
         * @return
         */
        @JvmStatic
        fun json(jsonObject: JsonObject): HttpRequestBody {
            return json(jsonObject.toString())
        }

        @JvmOverloads
        fun json(json: String, encoding: String = "UTF-8"): HttpRequestBody {
            return try {
                HttpRequestBody(json.toByteArray(charset(encoding)), ContentType.JSON, encoding)
            } catch (e: UnsupportedEncodingException) {
                throw IllegalArgumentException("illegal encoding $encoding", e)
            }
        }

        fun custom(body: ByteArray?, contentType: String?, encoding: String?): HttpRequestBody {
            return HttpRequestBody(body, contentType, encoding)
        }

        fun form(params: Map<String?, Any>, encoding: String): HttpRequestBody {
            val nameValuePairs: MutableList<NameValuePair> = ArrayList(params.size)
            for ((key, value) in params) {
                nameValuePairs.add(BasicNameValuePair(key, value.toString()))
            }
            return try {
                HttpRequestBody(
                    URLEncodedUtils.format(nameValuePairs, encoding).toByteArray(charset(encoding)),
                    ContentType.FORM,
                    encoding
                )
            } catch (e: UnsupportedEncodingException) {
                throw IllegalArgumentException("illegal encoding $encoding", e)
            }
        }
    }

    object ContentType {
        const val JSON = "application/json"
        const val FORM = "application/x-www-form-urlencoded"
        const val MULTIPART = "multipart/form-data"
    }
}