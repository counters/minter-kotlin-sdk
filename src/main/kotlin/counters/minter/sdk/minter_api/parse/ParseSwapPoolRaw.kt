package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import org.json.JSONObject

class ParseSwapPoolRaw {

    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): MinterRaw.SwapPoolRaw? {

//        println(result)
        val amount0 = result.getString("amount0")
        val amount1 = result.getString("amount1")
        val liquidity = result.getString("liquidity")

        return MinterRaw.SwapPoolRaw(
            minterMatch.getAmount(amount0),
            minterMatch.getAmount(amount1),
            minterMatch.getAmount(liquidity)
        )
    }
}