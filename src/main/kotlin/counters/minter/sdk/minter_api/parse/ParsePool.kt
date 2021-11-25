package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import org.json.JSONObject

class ParsePool {
    private val minterMatch = MinterMatch()


    fun getRaw(result: JSONObject): MinterRaw.PoolRaw? {


        val data = result.getJSONObject("data")

        val coin0 = CoinObjClass.fromJson(data.getJSONObject("coin0"))
        val coin1 = CoinObjClass.fromJson(data.getJSONObject("coin1"))
        val volume0 = data.getString("volume0")
        val volume1 = data.getString("volume1")

        val tags = result.getJSONObject("tags")

        val id = tags.getInt("tx.pool_id")
        val liquidity = tags.getString("tx.liquidity")

        val pool_token = tags.getString("tx.pool_token")
        val pool_token_id = tags.getLong("tx.pool_token_id")

        val token = CoinObjClass.CoinObj(pool_token_id, pool_token)

        if (coin0!=null && coin1!=null)
        return MinterRaw.PoolRaw(
            id,
            coin0,
            coin1,
            minterMatch.getAmount(volume0),
            minterMatch.getAmount(volume1),
            minterMatch.getAmount(liquidity),
            token,
        )

        return null
    }
}