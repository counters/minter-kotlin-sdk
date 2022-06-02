package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.BestTrade
import org.json.JSONObject

class ParseBestTrade {
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): BestTrade? {
        val jsonPath = result.getJSONArray("path")
        val amount = result.getString("result")
        val path = jsonPath.map {
            (it as String).toLong()
        }
        return BestTrade(
            path = path,
            result = minterMatch.getAmount(amount),
        )
    }
}
