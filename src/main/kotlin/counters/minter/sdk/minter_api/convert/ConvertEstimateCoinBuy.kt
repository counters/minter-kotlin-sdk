package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.EstimateCoinBuyResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.MinterMatch

class ConvertEstimateCoinBuy: MinterMatch() {

    private val convertSwapFrom = ConvertSwapFrom()

    fun get(response: EstimateCoinBuyResponse): Coin.EstimateCoin {
        return Coin.EstimateCoin(
            getAmount(response.willPay),
            getAmount(response.commission),
            convertSwapFrom.convSwapFrom(response.swapFrom)
        )
    }
}
