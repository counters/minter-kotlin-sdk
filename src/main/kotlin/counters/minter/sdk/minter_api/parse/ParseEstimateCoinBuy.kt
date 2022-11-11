package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.enums.SwapFromTypes
import org.json.JSONObject

class ParseEstimateCoinBuy {
    private val minterMatch = MinterMatch()

    fun getOld(result: JSONObject): Coin.EstimateCoinBuy? {
//        var coin: Coin.EstimateCoinBuy? = null

        val will_pay = result.getString("will_pay")
        val commission = result.getString("commission")


        val coin = Coin.EstimateCoinBuy(minterMatch.getAmount(will_pay), minterMatch.getAmount(commission))
        return coin
    }

    fun get(result: JSONObject): Coin.EstimateCoin {
//        println(result)
//        var coin: Coin.EstimateCoinBuy? = null
        val will_pay = result.getString("will_pay") //
        val commission = result.getString("commission")
        val swap_from = result.getString("swap_from")
        val swap_fromType = SwapFromTypes.values().firstOrNull { swap_from == it.value }!!

        val coin = Coin.EstimateCoin(minterMatch.getAmount(will_pay), minterMatch.getAmount(commission), swap_fromType)
        return coin
    }

}
