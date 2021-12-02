package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.LimitOrderResponse
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterMatch

class ConvertLimitOrder {

    private var minterMatch = MinterMatch()

    fun get(response: LimitOrderResponse): LimitOrderRaw {
//        minterMatch.getAmount("0")
        return LimitOrderRaw(
            id = response.id,
            coinSell = CoinObjClass.CoinObj(response.coinSell.id, response.coinSell.symbol),
            wantSell = minterMatch.getAmount(response.wantSell),
            coinBuy = CoinObjClass.CoinObj(response.coinBuy.id, response.coinBuy.symbol),
            wantBuy = minterMatch.getAmount(response.wantBuy),
            price = response.price.toDouble(),
            owner = response.owner,
            height = response.height,
        )
    }
}
