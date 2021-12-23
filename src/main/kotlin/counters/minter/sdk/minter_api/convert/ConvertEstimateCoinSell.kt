package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.EstimateCoinSellResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.MinterMatch

class ConvertEstimateCoinSell: MinterMatch() {
    private val convertSwapFrom = ConvertSwapFrom()

    fun get(response: EstimateCoinSellResponse): Coin.EstimateCoin {
        return Coin.EstimateCoin(
            getAmount(response.willGet),
            getAmount(response.commission),
            convertSwapFrom.convSwapFrom(response.swapFrom)
        )
    }
/*    fun get(response: EstimateCoinSellAllResponse): Coin.EstimateCoin {
        return Coin.EstimateCoin(
            getAmount(response.willGet),
            getAmount(response.commission),
            convertSwapFrom.convSwapFrom(response.swapFrom)
        )
    }*/

}
