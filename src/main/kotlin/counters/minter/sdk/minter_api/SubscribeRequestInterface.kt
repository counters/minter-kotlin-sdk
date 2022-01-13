package counters.minter.sdk.minter_api

import counters.minter.grpc.client.SubscribeRequest

interface SubscribeRequestInterface {

    fun getRequestSubscribe(query: String): SubscribeRequest {
        return SubscribeRequest.newBuilder().setQuery(query).build()
    }
}
