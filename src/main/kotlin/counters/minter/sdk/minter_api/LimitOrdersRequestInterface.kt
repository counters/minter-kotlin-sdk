package counters.minter.sdk.minter_api

import counters.minter.grpc.client.LimitOrdersRequest

interface LimitOrdersRequestInterface {

    fun request(ids: List<Long>, height: Long?=null, deadline: Long?=null): LimitOrdersRequest? {
        val requestBuilder = LimitOrdersRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        ids.forEachIndexed { index, id ->
            requestBuilder.setIds(index, id)
        }
        return requestBuilder.build()
    }
}