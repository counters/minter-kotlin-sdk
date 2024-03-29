package counters.minter.sdk.minter_api

import counters.minter.grpc.client.LimitOrdersRequest

interface LimitOrdersRequestInterface {

    fun getRequestLimitOrders(ids: List<Long>?=null, height: Long?=null): LimitOrdersRequest {
        val requestBuilder = LimitOrdersRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        ids?.let { requestBuilder.addAllIds(it) }
/*        ids.forEachIndexed { index, id ->
            requestBuilder.addIds(id)
        }*/
        return requestBuilder.build()
    }

}