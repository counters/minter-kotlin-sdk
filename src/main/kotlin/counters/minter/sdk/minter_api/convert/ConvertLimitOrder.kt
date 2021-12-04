package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.LimitOrderResponse
import counters.minter.grpc.client.LimitOrdersOfPoolResponse
import counters.minter.grpc.client.LimitOrdersResponse
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterMatch

class ConvertLimitOrder {

    private var minterMatch = MinterMatch()

    fun get(response: LimitOrderResponse): LimitOrderRaw {
        return LimitOrderRaw(
            id = response.id,
            coinSell = CoinObjClass.CoinObj(response.coinSell.id, response.coinSell.symbol),
            wantSell = minterMatch.getAmount(response.wantSell),
            coinBuy = CoinObjClass.CoinObj(response.coinBuy.id, response.coinBuy.symbol),
            wantBuy = minterMatch.getAmount(response.wantBuy),
            price = response.price.toDouble(),
            owner = response.owner,
            height = response.height,
            pool_id = null
        )
    }

    fun getList(ordersList: List<LimitOrderResponse>): List<LimitOrderRaw> {
        val array = arrayListOf<LimitOrderRaw>()
        ordersList.forEach {
            array.add(this.get(it))
        }
        return array
    }

    fun getList(response: LimitOrdersResponse) = getList(response.ordersList)
    fun getList(response: LimitOrdersOfPoolResponse) = getList(response.ordersList)

}