package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.EstimateCoinSellAllResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.MinterMatch

class ConvertEstimateCoinSellAll: MinterMatch() {
    private val convertSwapFrom = ConvertSwapFrom()

    fun get(response: EstimateCoinSellAllResponse): Coin.EstimateCoin {
        return Coin.EstimateCoin(
            getAmount(response.willGet),
            null,
            convertSwapFrom.convSwapFrom(response.swapFrom)
        )
    }
}
