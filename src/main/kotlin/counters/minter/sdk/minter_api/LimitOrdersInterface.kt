package counters.minter.sdk.minter_api

import counters.minter.grpc.client.*
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import java.util.concurrent.TimeUnit

interface LimitOrdersInterface : LimitOrdersRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertLimitOrder: ConvertLimitOrder

    fun getLimitOrdersGrpc(request: LimitOrdersRequest, deadline: Long? = null): LimitOrdersResponse? {
        blockingClient.limitOrders(request)?.let {
            return it
        } ?: run {
            return null
        }
    }

    fun getLimitOrdersGrpc(ids: List<Long>, height: Long? = null, deadline: Long? = null) = getLimitOrdersGrpc(request(ids, height), deadline)

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
        getLimitOrdersGrpc(request(ids, height), deadline, result)


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
