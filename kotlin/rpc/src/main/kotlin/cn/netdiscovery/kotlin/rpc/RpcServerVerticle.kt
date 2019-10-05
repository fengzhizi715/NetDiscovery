package cn.netdiscovery.kotlin.rpc

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.coroutines.toChannel
import kotlinx.coroutines.launch

/**
 * Created by tony on 2019-10-06.
 */
class RpcServerVerticle(private val channel: String) : CoroutineVerticle() {
    private val services: HashMap<String, RpcServiceInstance> = hashMapOf()

    override suspend fun start() {
        launch(vertx.dispatcher()) {
            for (msg in vertx.eventBus().consumer<ByteArray>(channel).toChannel(vertx)) {
                // Start a new coroutine to handle the incoming request to support recursive call
                launch(vertx.dispatcher()) {
                    try {
                        with(msg.body().toRpcRequest()) {
                            msg.reply(services[service]?.processRequest(this)?.toBytes()
                                    ?: throw NoSuchElementException("Service $service not found"))
                        }
                    } catch (e: Throwable) {
                        msg.fail(1, e.message)
                    }
                }
            }
        }
    }

    /**
     * Register the service object
     * @param name Name of the service
     * @param impl Object which implements the service
     * @return The RpcServerVerticle instance to support fluent call
     */
    fun <T : Any> register(name: String, impl: T): RpcServerVerticle {
        services[name] = RpcServiceInstance.instance(impl)
        return this
    }
}