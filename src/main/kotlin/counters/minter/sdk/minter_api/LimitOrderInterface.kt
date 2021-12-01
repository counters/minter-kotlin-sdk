package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrderResponse

interface LimitOrderInterface: LimitOrderRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    //    val convertTransaction: ConvertTransaction
    fun getLimitOrder(orderId: Long, height: Long?=null, deadline: Long?=null): LimitOrderResponse? {
        val request = get(orderId, height)
        blockingClient.limitOrder(request)?.let {
            return it
        } ?: run {
            return null
        }
    }

}
