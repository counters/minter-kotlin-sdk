package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrdersOfPoolResponse
import counters.minter.sdk.minter_api.convert.ConvertTransaction

interface LimitOrdersOfPoolInterface: LimitOrdersOfPoolRequestInterface  {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

//    val convertTransaction: ConvertTransaction

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): LimitOrdersOfPoolResponse? {
        val request = request(sellCoin, buyCoin, limit, height)
        blockingClient.limitOrdersOfPool(request)?.let {
            return it
        } ?: run {
            return null
        }
    }
}