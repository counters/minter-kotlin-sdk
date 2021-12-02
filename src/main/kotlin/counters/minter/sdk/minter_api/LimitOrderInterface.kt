package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrderRequest
import counters.minter.grpc.client.LimitOrderResponse
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface LimitOrderInterface: LimitOrderRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertLimitOrder: ConvertLimitOrder

    val logger: KLogger

    fun getLimitOrderGrpc(request: LimitOrderRequest, deadline: Long?=null): LimitOrderResponse? {
        try {
            blockingClient.limitOrder(request)?.let {
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

    fun getLimitOrderGrpc(request: LimitOrderRequest, deadline: Long? = null, result: ((result: LimitOrderResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.limitOrder(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getLimitOrderGrpc(orderId: Long, height: Long?=null, deadline: Long?=null): LimitOrderResponse? {
        val request = getRequestLimitOrder(orderId, height)
        return getLimitOrderGrpc(request, deadline)
    }

    fun getLimitOrder(orderId: Long, height: Long?=null, deadline: Long?=null): LimitOrderRaw? {
        getLimitOrderGrpc(orderId, height, deadline)?.let {
            return convertLimitOrder.get(it)
        }
        return null
    }

    fun getLimitOrder(orderId: Long, height: Long?=null, deadline: Long?=null, result: ((result: LimitOrderRaw?) -> Unit)) {
        getLimitOrderGrpc(getRequestLimitOrder(orderId, height), deadline) {
            it?.let { result(convertLimitOrder.get(it)) } ?: run { result(null) }
        }
    }

}
