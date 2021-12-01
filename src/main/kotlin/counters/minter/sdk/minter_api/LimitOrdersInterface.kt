package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.LimitOrdersResponse
import counters.minter.sdk.minter_api.convert.ConvertTransaction

interface LimitOrdersInterface: LimitOrdersRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub
//    val convertTransaction: ConvertTransaction

    fun getLimitOrders(ids: List<Long>, height: Long?=null, deadline: Long?=null): LimitOrdersResponse? {
        val request = request(ids, height)
        blockingClient.limitOrders(request)?.let {
            return it
        } ?: run {
            return null
        }
    }

}
