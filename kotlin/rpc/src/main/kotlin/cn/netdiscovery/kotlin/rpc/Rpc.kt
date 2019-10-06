package cn.netdiscovery.kotlin.rpc

import java.io.ByteArrayOutputStream

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import java.io.ByteArrayInputStream
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by tony on 2019-10-06.
 */
private val kryo = Kryo().apply {
    isRegistrationRequired = false
}

@Suppress("ArrayInDataClass")
data class RpcRequest(val service: String = "",
                      val method: String = "",
                      val args: Array<out Any?> = arrayOf()) {
    fun toBytes(): ByteArray = ByteArrayOutputStream().use {
        val out = Output(it)
        kryo.writeObject(out, this)
        out.flush()
        out.buffer
    }

    fun toBuffer(): Buffer = Buffer.buffer(toBytes())
}

fun ByteArray.toRpcRequest(): RpcRequest = ByteArrayInputStream(this).use {
    (kryo.readObject(Input(it), RpcRequest::class.java) as RpcRequest)
}

fun Buffer.toRpcRequest(): RpcRequest = bytes.toRpcRequest()

data class RpcResponse(val response: Any? = null, val trace: Json? = null) {
    fun toBytes(): ByteArray = ByteArrayOutputStream().use {
        val out = Output(it)
        kryo.writeObject(out, this)
        out.flush()
        out.buffer
    }

    fun toBuffer(): Buffer = Buffer.buffer(toBytes())
}

fun ByteArray.toRpcResponse(): RpcResponse = ByteArrayInputStream(this).use {
    (kryo.readObject(Input(it), RpcResponse::class.java) as RpcResponse)
}

fun Buffer.toRpcResponse(): RpcResponse = bytes.toRpcResponse()

inline fun <reified T : Any> getProxyWithBlock(name: String, crossinline block: (RpcRequest, Continuation<Any?>) -> Unit) =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args: Array<Any?> ->
            val lastArg = args.lastOrNull()
            if (lastArg is Continuation<*>) {
                // The last argument of a suspend function is the Continuation object
                @Suppress("UNCHECKED_CAST") val cont = lastArg as Continuation<Any?>
                val argsButLast = args.take(args.size - 1)
                // Call the block with the request and the continuation
                block(RpcRequest(name, method.name, argsButLast.toTypedArray()), cont)
                // Suspend the coroutine to wait for the reply
                COROUTINE_SUSPENDED
            } else {
                // The function is not suspend
                null
            }
        } as T

/**
 * Dynamically create the service proxy object for the given interface
 * @param vertx Vertx instance
 * @param channel Name of the channel where RPC service listening
 * @param name Name of the service
 * @return RPC proxy object implements T
 */
inline fun <reified T : Any> getServiceProxy(vertx: Vertx, channel: String, name: String) =
        getProxyWithBlock(name) { req, cont ->
            vertx.eventBus().send(channel, req.toBytes(), Handler<AsyncResult<Message<ByteArray>>> { event ->
                // Resume the suspended coroutine on reply
                if (event?.succeeded() == true) {
                    cont.resume(event.result().body().toRpcResponse().response)
                } else {
                    cont.resumeWithException(event?.cause() ?: Exception("Unknown error"))
                }
            })
        } as T

/**
 * Dynamically create the async service proxy object for the given interface
 * Every method in the interface must return futures instead of direct value.
 *
 * @param vertx Vertx instance
 * @param channel Name of the channel where RPC service listening
 * @param name Name of the service
 * @param clazz Java class of the service interface
 * @return Async RPC proxy object implements T
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> getAsyncServiceProxy(vertx: Vertx, channel: String, name: String, clazz: Class<T>) =
        Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, method, args: Array<Any?> ->
            val future = Future.future<Message<ByteArray>>()
            vertx.eventBus().send(channel, RpcRequest(name, method.name, args).toBytes(), future.completer())
            future.map {
                it.body().toRpcResponse().response
            }
        } as T

/**
 * Dynamically create the service proxy object for the given interface
 * @param vertx Vertx instance
 * @param endpoint HTTP endpoint of the RPC service
 * @param name Name of the service
 * @param requestBuilder A function to customize HTTP request before it being sent
 * @return RPC proxy object implements T
 */
inline fun <reified T : Any> getHttpServiceProxy(vertx: Vertx, endpoint: String, name: String, crossinline requestBuilder: (HttpRequest<Buffer>) -> Any? = { _ -> }): T {
    val client = WebClient.create(vertx)
    return getProxyWithBlock(name) { req, cont ->
        client.postAbs(endpoint)
                .apply { requestBuilder(this) }
                .putHeader("content-type", "")
                .sendBuffer(req.toBuffer()) {
                    if (it.succeeded()) {
                        cont.resume(it.result().bodyAsBuffer().toRpcResponse().response)
                    } else {
                        cont.resumeWithException(it?.cause() ?: Exception("Unknown error"))
                    }
                }
    }
}

/**
 * Dynamically create the async service proxy object for the given interface
 * Every method in the interface must return futures instead of direct value.
 *
 * @param vertx Vertx instance
 * @param endpoint HTTP endpoint of the RPC service
 * @param name Name of the service
 * @param requestBuilder A function to customize HTTP request before it being sent
 * @param clazz Java class of the service interface
 * @return Async RPC proxy object implements T
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> getAsyncHttpServiceProxy(vertx: Vertx, endpoint: String, name: String, requestBuilder: RequestBuilder, clazz: Class<T>): T {
    val client = WebClient.create(vertx)

    return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, method, args: Array<Any?> ->
        val future = Future.future<HttpResponse<Buffer>>()
        client.postAbs(endpoint)
                .apply { requestBuilder.build(this) }
                .putHeader("content-type", "")
                .sendBuffer(RpcRequest(name, method.name, args).toBuffer(), future.completer())
        future.map {
            it.bodyAsBuffer().toRpcResponse().response
        }
    } as T
}


interface RequestBuilder {
    fun build(request: HttpRequest<Buffer>)

    companion object {
        val defaultBuilder: RequestBuilder = object : RequestBuilder {
            override fun build(request: HttpRequest<Buffer>) {}
        }
    }
}

object ServiceProxyFactory {
    /**
     * Dynamically create the service proxy object for the given interface
     * @param vertx Vertx instance
     * @param channel Name of the channel where RPC service listening
     * @param name Name of the service
     * @param clazz Java class of the service interface
     * @return RPC proxy object implements T
     */
    @JvmStatic
    fun <T : Any> getAsyncServiceProxy(vertx: Vertx, channel: String, name: String, clazz: Class<T>) = cn.netdiscovery.kotlin.rpc.getAsyncServiceProxy(vertx, channel, name, clazz)

    /**
     * Dynamically create the async service proxy object for the given interface
     * Every method in the interface must return futures instead of direct value.
     *
     * @param vertx Vertx instance
     * @param channel Name of the channel where RPC service listening
     * @param name Name of the service
     * @return Async RPC proxy object implements T
     */
    inline fun <reified T : Any> getServiceProxy(vertx: Vertx, channel: String, name: String) = cn.netdiscovery.kotlin.rpc.getServiceProxy<T>(vertx, channel, name)

    /**
     * Dynamically create the async service proxy object for the given interface
     * Every method in the interface must return futures instead of direct value.
     *
     * @param vertx Vertx instance
     * @param endpoint HTTP endpoint of the RPC service
     * @param name Name of the service
     * @param requestBuilder A function to customize HTTP request before it being sent
     * @param clazz Java class of the service interface
     * @return Async RPC proxy object implements T
     */
    @JvmStatic
    fun <T : Any> getAsyncHttpServiceProxy(vertx: Vertx, endpoint: String, name: String, requestBuilder: RequestBuilder, clazz: Class<T>): T = cn.netdiscovery.kotlin.rpc.getAsyncHttpServiceProxy(vertx, endpoint, name, requestBuilder, clazz)

    /**
     * Dynamically create the async service proxy object for the given interface
     * Every method in the interface must return futures instead of direct value.
     *
     * @param vertx Vertx instance
     * @param endpoint HTTP endpoint of the RPC service
     * @param name Name of the service
     * @param clazz Java class of the service interface
     * @return Async RPC proxy object implements T
     */
    @JvmStatic
    fun <T : Any> getAsyncHttpServiceProxy(vertx: Vertx, endpoint: String, name: String, clazz: Class<T>): T = getAsyncHttpServiceProxy(vertx, endpoint, name, RequestBuilder.defaultBuilder, clazz)

    /**
     * Dynamically create the service proxy object for the given interface
     * @param vertx Vertx instance
     * @param endpoint HTTP endpoint of the RPC service
     * @param name Name of the service
     * @param requestBuilder A function to customize HTTP request before it being sent
     * @return RPC proxy object implements T
     */
    inline fun <reified T : Any> getHttpServiceProxy(vertx: Vertx, endpoint: String, name: String, crossinline requestBuilder: (HttpRequest<Buffer>) -> Any? = { _ -> }) = cn.netdiscovery.kotlin.rpc.getHttpServiceProxy<T>(vertx, endpoint, name, requestBuilder)
}