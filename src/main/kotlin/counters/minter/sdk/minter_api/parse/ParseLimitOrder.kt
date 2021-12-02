package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterMatch
import org.json.JSONObject

class ParseLimitOrder {
    private var minterMatch = MinterMatch()

    fun get(result: JSONObject): LimitOrderRaw {
        val id = result.getLong("id")
        val coinSell = CoinObjClass.fromJson(result.getJSONObject("coin_sell"))!!
        val coinBuy = CoinObjClass.fromJson(result.getJSONObject("coin_buy"))!!
        val wantSell = minterMatch.getAmount(result.getString("want_sell"))
        val wantBuy = minterMatch.getAmount(result.getString("want_buy"))
        val price = result.getDouble("price")
        val owner = result.getString("owner")
        val height = result.getLong("height")

        return LimitOrderRaw(
            id = id,
            coinSell = coinSell,
            wantSell = wantSell,
            coinBuy = coinBuy,
            wantBuy = wantBuy,
            price = price,
            owner = owner,
            height = height,
        )
    }

    fun array(result: JSONObject): List<LimitOrderRaw>? {
        result.optJSONArray("orders")?.let {
            val array = arrayListOf<LimitOrderRaw>()
            it.forEach {
                get(it as JSONObject)?.let {
                    array.add(it)
                }
            }
            return array
        }
        return null
    }

}