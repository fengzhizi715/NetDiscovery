package cn.netdiscovery.kotlin.rpc

import kotlin.reflect.full.callSuspend

/**
 * Created by tony on 2019-10-06.
 */
internal interface RpcServiceInstance {
    suspend fun processRequest(request: RpcRequest): RpcResponse

    companion object {
        fun <T : Any> instance(impl: T): RpcServiceInstance {
            return object : RpcServiceInstance {
                override suspend fun processRequest(request: RpcRequest): RpcResponse {
                    val ret = impl::class.members.first {
                        // TODO: Check signature to support overloading
                        it.name == request.method
                    }.callSuspend(impl, *(request.args))
                    return RpcResponse(ret)
                }
            }
        }
    }
}