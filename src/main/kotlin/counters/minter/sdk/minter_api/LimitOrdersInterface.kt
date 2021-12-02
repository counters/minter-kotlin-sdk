package counters.minter.sdk.minter_api

import counters.minter.grpc.client.*
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface LimitOrdersInterface : LimitOrdersRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertLimitOrder: ConvertLimitOrder
    val logger: KLogger

    fun getLimitOrdersGrpc(request: LimitOrdersRequest, deadline: Long? = null): LimitOrdersResponse? {
        try {
            blockingClient.limitOrders(request)?.let {
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

    fun getLimitOrdersGrpc(ids: List<Long>, height: Long? = null, deadline: Long? = null) = getLimitOrdersGrpc(getRequestLimitOrders(ids, height), deadline)

    fun getLimitOrdersGrpc(request: LimitOrdersRequest, deadline: Long? = null, result: ((result: LimitOrdersResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.limitOrders(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getLimitOrdersGrpc(ids: List<Long>, height: Long? = null, deadline: Long? = null, result: ((result: LimitOrdersResponse?) -> Unit)) =
        getLimitOrdersGrpc(getRequestLimitOrders(ids, height), deadline, result)


    fun getLimitOrders(ids: List<Long>, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersGrpc(ids, height, deadline)?.let {
            return convertLimitOrder.getList(it.ordersList)
        } ?: run {
            return null
        }
    }


    fun getLimitOrders(ids: List<Long>, height: Long? = null, deadline: Long? = null, result: ((result: List<LimitOrderRaw>?) -> Unit)) {
        getLimitOrdersGrpc(ids, height, deadline) {
            it?.let { result(convertLimitOrder.getList(it.ordersList)) } ?: run { result(null) }
        }
    }

}
