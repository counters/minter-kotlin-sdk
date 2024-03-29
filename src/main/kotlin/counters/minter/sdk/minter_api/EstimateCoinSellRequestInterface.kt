package counters.minter.sdk.minter_api

import counters.minter.grpc.client.EstimateCoinSellRequest
import counters.minter.sdk.minter.enums.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertSwapFrom

interface EstimateCoinSellRequestInterface {

    val convertSwapFrom: ConvertSwapFrom

    fun getRequestEstimateCoinSell(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
    ): EstimateCoinSellRequest {
        val requestBuilder = EstimateCoinSellRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        coin_id_commission?.let { requestBuilder.setCoinIdCommission(it) }
        swap_from?.let { requestBuilder.setSwapFrom(convertSwapFrom.convSwapFrom(it)) }
        route?.let { requestBuilder.addAllRoute(it) }
        return requestBuilder.setCoinIdToSell(coinToSell).setValueToSell(valueToSell).setCoinIdToBuy(coinToBuy).build()
    }

}
