package counter.minter_sdk.MinterApi

import counter.minter_sdk.Minter.Coin
import counter.minter_sdk.Minter.Enum.SwapFromTypes
import counter.minter_sdk.Minter.MinterMatch
import org.json.JSONObject

class ParseEstimateCoinSell {
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): Coin.EstimateCoinSell? {

        val will_get = result.getString("will_get")
        val commission = result.getString("commission")
        val swap_from = result.getString("swap_from")

        val swap_fromType = SwapFromTypes.values().firstOrNull { swap_from == it.value }

        if (swap_fromType != null)
            return Coin.EstimateCoinSell(
                minterMatch.getAmount(will_get),
                minterMatch.getAmount(commission),
                swap_fromType
            )
        else return null
    }
}