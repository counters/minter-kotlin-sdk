package counters.minter.sdk.minter_api

import counters.minter.grpc.client.SwapPoolProviderRequest

interface SwapPoolProviderRequestInterface {

    fun getRequestSwapPool(coin0: Long, coin1: Long, provider: String, height: Long? = null): SwapPoolProviderRequest {
        val requestBuilder = SwapPoolProviderRequest.newBuilder()
//        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setCoin0(coin0).setCoin1(coin1).setProvider(provider).build()
    }
}
