package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrdersOfPoolRequest
import counters.minter.grpc.client.LimitOrdersOfPoolResponse
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import java.util.concurrent.TimeUnit

interface LimitOrdersOfPoolInterface : LimitOrdersOfPoolRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertLimitOrder: ConvertLimitOrder

    fun getLimitOrdersOfPoolGrpc(request: LimitOrdersOfPoolRequest, deadline: Long? = null): LimitOrdersOfPoolResponse? {
        blockingClient.limitOrdersOfPool(request)?.let {
            return it
        } ?: run {
            return null
        }
    }

    fun getLimitOrdersOfPoolGrpc(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null) =
        getLimitOrdersOfPoolGrpc(request(sellCoin, buyCoin, limit, height))

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersOfPoolGrpc(sellCoin, buyCoin, limit, height, deadline)?.let {
            return convertLimitOrder.getList(it.ordersList)
        } ?: run {
            return null
        }
    }

    fun getLimitOrdersOfPoolGrpc(request: LimitOrdersOfPoolRequest?, deadline: Long?, result: (result: LimitOrdersOfPoolResponse?) -> Unit) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.limitOrdersOfPool(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getLimitOrdersOfPoolGrpc(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, deadline: Long?, result: (result: LimitOrdersOfPoolResponse?) -> Unit) =
        getLimitOrdersOfPoolGrpc(request(sellCoin, buyCoin, limit, height, deadline), deadline, result)

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, deadline: Long?, result: (result: List<LimitOrderRaw>?) -> Unit) {
        getLimitOrdersOfPoolGrpc(sellCoin, buyCoin, limit, height, deadline){
            it?.let { result(convertLimitOrder.getList(it.ordersList)) } ?: run { result(null) }
        }
    }

}