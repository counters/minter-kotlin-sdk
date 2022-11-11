package counters.minter.sdk.minter_api

import counters.minter.grpc.client.EstimateCoinSellAllRequest
import counters.minter.sdk.minter.enums.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertSwapFrom

interface EstimateCoinSellAllRequestInterface {

    val convertSwapFrom: ConvertSwapFrom

    fun getRequestEstimateCoinSellAll(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
    ): EstimateCoinSellAllRequest {
        val requestBuilder = EstimateCoinSellAllRequest.newBuilder()
        height?.let { requestBuilder.setHeight(it) }
        gas_price?.let { requestBuilder.setGasPrice(it.toLong()) }
        swap_from?.let { requestBuilder.setSwapFrom(convertSwapFrom.convSwapFrom(it)) }
        route?.let { requestBuilder.addAllRoute(it) }
        return requestBuilder.setCoinIdToSell(coinToSell).setValueToSell(valueToSell).setCoinIdToBuy(coinToBuy).build()
    }
}
