package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterMatch
import org.json.JSONObject

class ParseLimitOrder {
    private var minterMatch = MinterMatch()

    /*
    {
        "id": "121",
        "coin_sell": {
        "id": "1902",
        "symbol": "HUB"
    },
        "coin_buy": {
        "id": "0",
        "symbol": "BIP"
    },
        "want_sell": "1000000000000000000",
        "want_buy": "18999900000000000000000",
        "price": "0.0000526318559571366165085079395155",
        "owner": "Mx0903ab168597a7c86ad0d4b72424b3632be0af1b",
        "height": "7696427"
    }
    */
    fun get(result: JSONObject): LimitOrderRaw {
//        println("result $result")
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
/*    return LimitOrderRaw(
    id = response.id,
    coinSell = CoinObjClass.CoinObj(response.coinSell.id, response.coinSell.symbol),
    wantSell = minterMatch.getAmount(response.wantSell),
    coinBuy = CoinObjClass.CoinObj(response.coinBuy.id, response.coinBuy.symbol),
    wantBuy = minterMatch.getAmount(response.wantBuy),
    price = minterMatch.getAmount(response.price),
    owner = response.owner,
    height = response.height,
    )*/
    fun array(result: JSONObject): List<LimitOrderRaw>? {
//        println("result $result")
        result.optJSONArray("orders")?.let {
//            println("orders $it")
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