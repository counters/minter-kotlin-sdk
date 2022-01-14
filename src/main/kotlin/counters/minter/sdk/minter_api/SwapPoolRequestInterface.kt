package counters.minter.sdk.minter_api

import counters.minter.grpc.client.SwapPoolRequest

interface SwapPoolRequestInterface {

    fun getRequestSwapPool(coin0: Long, coin1: Long, height: Long? = null): SwapPoolRequest {
        val requestBuilder = SwapPoolRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setCoin0(coin0).setCoin1(coin1).build()
    }
}
