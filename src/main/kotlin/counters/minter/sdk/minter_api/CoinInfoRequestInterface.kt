package counters.minter.sdk.minter_api

import counters.minter.grpc.client.CoinIdRequest
import counters.minter.grpc.client.CoinInfoRequest

interface CoinInfoRequestInterface {
    fun getRequestCoinInfo(symbol: String, height: Long? = null): CoinInfoRequest {
        val requestBuilder = CoinInfoRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setSymbol(symbol).build()
    }

    fun getRequestCoinInfo(coinId: Long, height: Long? = null): CoinIdRequest {
        val requestBuilder = CoinIdRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setId(coinId).build()
    }
}
