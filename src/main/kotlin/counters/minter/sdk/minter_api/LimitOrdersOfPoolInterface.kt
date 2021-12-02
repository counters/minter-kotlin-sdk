package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrdersOfPoolRequest
import counters.minter.grpc.client.LimitOrdersOfPoolResponse
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface LimitOrdersOfPoolInterface : LimitOrdersOfPoolRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertLimitOrder: ConvertLimitOrder
    val logger: KLogger

    fun getLimitOrdersOfPoolGrpc(request: LimitOrdersOfPoolRequest, deadline: Long? = null): LimitOrdersOfPoolResponse? {
        try {
            blockingClient.limitOrdersOfPool(request)?.let {
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

    fun getLimitOrdersOfPoolGrpc(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null) =
        getLimitOrdersOfPoolGrpc(getRequestLimitOrdersOfPool(sellCoin, buyCoin, limit, height))

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
        getLimitOrdersOfPoolGrpc(getRequestLimitOrdersOfPool(sellCoin, buyCoin, limit, height), deadline, result)

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, deadline: Long?, result: (result: List<LimitOrderRaw>?) -> Unit) {
        getLimitOrdersOfPoolGrpc(sellCoin, buyCoin, limit, height, deadline){
            it?.let { result(convertLimitOrder.getList(it.ordersList)) } ?: run { result(null) }
        }
    }

}