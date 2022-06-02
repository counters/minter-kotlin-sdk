package counters.minter.sdk.minter_api

import counters.minter.grpc.client.BestTradeRequest

interface BestTradeRequestInterface {

    fun getRequestBestTrade(sellCoin: Long, buyCoin: Long, amount: String, type: BestTradeRequest.Type, maxDepth: Int? = null, height: Long? = null): BestTradeRequest {
        val requestBuilder = BestTradeRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        maxDepth?.let { requestBuilder.setMaxDepth(it) }
        return requestBuilder.setSellCoin(sellCoin).setBuyCoin(buyCoin).setAmount(amount).setType(type).build()
    }
}
