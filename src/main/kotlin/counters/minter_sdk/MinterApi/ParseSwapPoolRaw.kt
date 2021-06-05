package counters.minter_sdk.MinterApi

import counters.minter_sdk.Minter.MinterMatch
import counters.minter_sdk.Minter.MinterRaw
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