package counters.minter.sdk.minter_api

import counters.minter.grpc.client.EstimateCoinBuyRequest
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertSwapFrom

interface EstimateCoinBuyRequestInterface {
    val convertSwapFrom: ConvertSwapFrom

    fun getRequestEstimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
    ): EstimateCoinBuyRequest {
        val requestBuilder = EstimateCoinBuyRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        coin_id_commission?.let { requestBuilder.setCoinIdCommission(it) }
        swap_from?.let { requestBuilder.setSwapFrom(convertSwapFrom.convSwapFrom(it)) }
        route?.let { requestBuilder.addAllRoute(it) }
        return requestBuilder.setCoinIdToSell(coinToSell).setValueToBuy(valueToBuy).setCoinIdToBuy(coinToBuy).build()
    }

}
