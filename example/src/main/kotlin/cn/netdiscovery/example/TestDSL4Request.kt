package cn.netdiscovery.example

import cn.netdiscovery.core.Spider
import cn.netdiscovery.core.pipeline.debug.DebugPipeline
import cn.netdiscovery.kotlin.dsl.request

import io.vertx.core.http.HttpMethod

/**
 * Created by tony on 2018/9/18.
 */
fun main() {

    val request = request {

        url = "https://www.baidu.com/"

        httpMethod = HttpMethod.GET

        spiderName = "tony"

        downloadDelay = 2000

        header {

            "111" to "2222"
            "333" to "44444"
        }

        extras {

            "tt" to "qqq"
        }
    }

    Spider.create().name("tony").request(request).pipeline(DebugPipeline()).run()
}