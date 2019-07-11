package MinterApi

import Minter.Coin
import Minter.MinterMatch
import org.json.JSONObject

class ParseEstimateCoinBuy {
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): Coin.EstimateCoinBuy? {
//        var coin: Coin.EstimateCoinBuy? = null

        val will_pay = result.getString("will_pay")
        val commission = result.getString("commission")

        val coin = Coin.EstimateCoinBuy(minterMatch.getAmount(will_pay), minterMatch.getAmount(commission))
        return coin
    }

}