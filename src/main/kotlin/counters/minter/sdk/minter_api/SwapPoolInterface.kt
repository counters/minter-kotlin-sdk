package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.SwapPoolRequest
import counters.minter.grpc.client.SwapPoolResponse
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter_api.convert.ConvertSwapPool
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface SwapPoolInterface : SwapPoolRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertSwapPool: ConvertSwapPool

    val logger: KLogger

    fun getSwapPoolGrpc(request: SwapPoolRequest, deadline: Long?): SwapPoolResponse? {
        try {
            blockingClient.swapPool(request)?.let {
                return it
            } ?: run {
                return null
            }
        } catch (e: StatusRuntimeException) {
            logger.warn { e }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getSwapPoolGrpc(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null) =
        getSwapPoolGrpc(getRequestSwapPool(coin0, coin1, height), deadline)

    fun getSwapPoolGrpc(request: SwapPoolRequest, deadline: Long? = null, result: (result: SwapPoolResponse?) -> Unit) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.swapPool(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getSwapPoolGrpc(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null, result: ((result: SwapPoolResponse?) -> Unit)) =
        getSwapPoolGrpc(getRequestSwapPool(coin0, coin1, height), deadline, result)

    /*    fun getSwapPool(coin1: Long, coin2: Long, height: Long?=null): MinterRaw.SwapPoolRaw? {
            TODO("Not yet implemented")
        }
        */
    fun getSwapPool(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null): MinterRaw.SwapPoolRaw? {
        getSwapPoolGrpc(coin0, coin1, height, deadline)?.let {
            return convertSwapPool.get(it)
        } ?: run {
            return null
        }
    }

    fun getSwapPool(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null, result: ((MinterRaw.SwapPoolRaw?) -> Unit)) {
        getSwapPoolGrpc(coin0, coin1, height, deadline) {
            it?.let { result(convertSwapPool.get(it)) } ?: run { result(null) }
        }
    }
}
