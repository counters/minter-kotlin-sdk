package counters.minter.sdk.MinterApi.parse

import counters.minter.sdk.Minter.Coin
import counters.minter.sdk.Minter.Enum.SwapFromTypes
import counters.minter.sdk.Minter.MinterMatch
import org.json.JSONObject

class ParseEstimateCoinSell {
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): Coin.EstimateCoin? {

        val will_get = result.getString("will_get")
        val commission = if (result.isNull("commission")) null else minterMatch.getAmount(result.getString("commission"))
        val swap_from = result.getString("swap_from")

        val swap_fromType = SwapFromTypes.values().firstOrNull { swap_from == it.value }

        if (swap_fromType != null)
            return Coin.EstimateCoin(
                minterMatch.getAmount(will_get),
                commission,
                swap_fromType
            )
        else return null
    }
}