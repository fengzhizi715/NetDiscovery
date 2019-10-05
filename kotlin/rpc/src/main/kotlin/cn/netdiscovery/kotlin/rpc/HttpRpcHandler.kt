package cn.netdiscovery.kotlin.rpc

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by tony on 2019-10-06.
 */
class HttpRpcHandler(private val vertx: Vertx) : Handler<RoutingContext>, CoroutineScope {
    private val services: HashMap<String, RpcServiceInstance> = hashMapOf()

    private val context by lazy {
        vertx.orCreateContext
    }

    override val coroutineContext by lazy {
        context.dispatcher()
    }

    override fun handle(event: RoutingContext?) {
        launch(vertx.dispatcher()) {
            val req = event?.body?.toRpcRequest() ?: throw IllegalArgumentException("Event is null")
            val resp = services[req.service]?.processRequest(req)
                    ?: throw NoSuchElementException("Service ${req.service} not found")
            event.response().putHeader("content-type", "application/octet-stream")
                    .end(resp.toBuffer())
        }
    }

    /**
     * Register the service object
     * @param name Name of the service
     * @param impl Object which implements the service
     * @return The RpcServerVerticle instance to support fluent call
     */
    fun <T : Any> register(name: String, impl: T): HttpRpcHandler {
        services[name] = RpcServiceInstance.instance(impl)
        return this
    }
}