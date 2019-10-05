package cn.netdiscovery.kotlin.rpc

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.WebClient
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaType
/**
 * Created by tony on 2019-10-06.
 */
val mapper: ObjectMapper = ObjectMapper()
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

annotation class HttpRequest(val method: HttpMethod = HttpMethod.POST, val path: String = "")
annotation class QueryParam(val value: String = "")

data class JsonRpcRequest(val name: String,
                          @JsonIgnore
                          @Transient
                          val method: KFunction<*>,
                          val args: Map<String, Any?>)

fun Any.stringify(): String = Json.encode(this)

@Suppress("unused")
fun HttpRequest<Buffer>.noop() {}

class JsonRpcException(val statusCode: Int = 0, message: String = "") : Throwable(message = message)

inline fun <reified T : Any> getJsonProxyWithBlock(crossinline block: (JsonRpcRequest, cn.netdiscovery.kotlin.rpc.HttpRequest?, Continuation<Any?>) -> Unit) =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args: Array<Any?> ->
            val lastArg = args.lastOrNull()
            if (lastArg is Continuation<*>) {
                // The last argument of a suspend function is the Continuation object
                @Suppress("UNCHECKED_CAST") val cont = lastArg as Continuation<Any?>
                val argsButLast = args.take(args.size - 1)

                // Use Kotlin reflection to get parameter names
                val mf = T::class.memberFunctions.firstOrNull { it.name == method.name }!!

                // Construct name->value parameter map
                val paramMap = mf.parameters
                        .takeLast(mf.parameters.size - 1)       // Skip first `this`
                        .map { it.name!! }
                        .zip(argsButLast)
                        .toMap()

                // Extract HttpRequest annotation if exist
                val ann = method.annotations.firstOrNull {
                    it is cn.netdiscovery.kotlin.rpc.HttpRequest
                } as? cn.netdiscovery.kotlin.rpc.HttpRequest

                // Call the block with the request and the continuation
                block(JsonRpcRequest(method.name, mf, paramMap), ann, cont)
                // Suspend the coroutine to wait for the reply
                COROUTINE_SUSPENDED
            } else {
                // The function is not suspend
                null
            }
        } as T

/**
 * Dynamically create the JSON RPC HTTP service proxy object for the given interface
 * @param vertx Vertx instance
 * @param endpoint HTTP endpoint of the RPC service
 * @param requestBuilder A function to customize HTTP request before it being sent
 * @return RPC proxy object implements T
 */
inline fun <reified T : Any> getHttpJsonRpcServiceProxy(vertx: Vertx,
                                                        endpoint: String,
                                                        crossinline requestBuilder: HttpRequest<Buffer>.() -> Unit = HttpRequest<Buffer>::noop): T {
    val client = WebClient.create(vertx)
    return getJsonProxyWithBlock { req, ann, cont ->
        // Split arguments into query params and body
        val queryParams: HashMap<String, Any?> = hashMapOf()
        val bodyParams: HashMap<String, Any?> = hashMapOf()
        for (p in req.method.parameters) {
            val qp = p.annotations.firstOrNull { it is QueryParam } as? QueryParam
            if (qp != null) {
                val qpName = if (qp.value.isBlank()) (p.name ?: continue) else qp.value
                if (p.name in req.args) {
                    queryParams[qpName] = req.args.getValue(p.name!!)
                }
            } else {
                if (p.name in req.args) {
                    bodyParams[p.name!!] = req.args.getValue(p.name!!)
                }
            }
        }
        val ep = endpoint.trimEnd('/')
        val method = ann?.method ?: HttpMethod.POST
        val request = client.requestAbs(method,
                if (ann == null || ann.path.isBlank()) "$ep/${req.method.name}" else "$ep/${ann.path}")
                .apply { this.requestBuilder() }
                .putHeader("content-type", "application/json; charset=utf-8")
                .apply {
                    queryParams.forEach { t, u ->
                        addQueryParam(t, u.toString())
                    }
                }
        // Don't send body when there is no body param or method is TRACE, standards forbid TRACE to have a body
        if (method == HttpMethod.TRACE || bodyParams.isEmpty()) {
            request.send {
                if (it.succeeded()) {
                    if (it.result().statusCode() >= 400)    // Status code>=400 means error
                        cont.resumeWithException(JsonRpcException(it.result().statusCode(), it.result().statusMessage()))
                    else
                        cont.resume(mapper.readValue(it.result().bodyAsBuffer().bytes, mapper.constructType(req.method.returnType.javaType)))
                } else {
                    cont.resumeWithException(it?.cause() ?: Exception("Unknown error"))
                }
            }
        } else {
            request.sendJson(bodyParams) {
                if (it.succeeded()) {
                    if (it.result().statusCode() >= 400)    // Status code>=400 means error
                        cont.resumeWithException(JsonRpcException(it.result().statusCode(), it.result().statusMessage()))
                    else
                        cont.resume(mapper.readValue(it.result().bodyAsBuffer().bytes, mapper.constructType(req.method.returnType.javaType)))
                } else {
                    cont.resumeWithException(it?.cause() ?: Exception("Unknown error"))
                }
            }
        }
    }
}