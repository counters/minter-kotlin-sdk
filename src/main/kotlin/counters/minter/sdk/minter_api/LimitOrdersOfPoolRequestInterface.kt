package counters.minter.sdk.minter_api

import counters.minter.grpc.client.LimitOrdersOfPoolRequest

interface LimitOrdersOfPoolRequestInterface {

    fun getRequestLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int?= null, height: Long?= null, deadline: Long?= null): LimitOrdersOfPoolRequest {
        val requestBuilder = LimitOrdersOfPoolRequest.newBuilder()
        limit?.let { requestBuilder.setLimit(it) }
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setSellCoin(sellCoin).setBuyCoin(buyCoin).build()
    }
}
