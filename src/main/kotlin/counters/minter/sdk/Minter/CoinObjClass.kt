package counters.minter.sdk.Minter

import org.json.JSONObject

object CoinObjClass {
    data class CoinObj(
        val id: Long,
        val symbol: String?
    )

    fun fromJson(coin: JSONObject): CoinObj? {
        if (!coin.isNull("id") || !coin.isNull("symbol"))
        return CoinObj(coin.getString("id").toLong(), coin.getString("symbol"))
        else return null
    }
}