package MinterApi

import Minter.Coin
import Minter.MinterMatch
import org.json.JSONObject

class ParseEstimateCoinSell {
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): Coin.EstimateCoinSell? {
//        var coin: Coin.EstimateCoinSell? = null

        val will_get = result.getString("will_get")
        val commission = result.getString("commission")

        val coin = Coin.EstimateCoinSell(minterMatch.getAmount(will_get), minterMatch.getAmount(commission))
        return coin
    }
}