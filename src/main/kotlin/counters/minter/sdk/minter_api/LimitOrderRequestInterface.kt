package counters.minter.sdk.minter_api

import counters.minter.grpc.client.LimitOrderRequest

interface LimitOrderRequestInterface {

    fun get(orderId: Long, height: Long?): LimitOrderRequest {
        val requestBuilder = LimitOrderRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setOrderId(orderId).build()
    }
}
