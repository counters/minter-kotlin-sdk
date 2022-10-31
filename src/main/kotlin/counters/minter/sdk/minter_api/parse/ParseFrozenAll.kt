package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.FrozenAllRaw
import org.json.JSONObject

class ParseFrozenAll: MinterMatch() {

    fun getRaw(result: JSONObject): List<FrozenAllRaw> {
        val array = ArrayList<FrozenAllRaw>()

        result.getJSONArray("frozen").forEach {
            it as JSONObject
            CoinObjClass.fromJson(it.getJSONObject("coin"))?.let { coin ->
                val item = FrozenAllRaw(
                    height = it.getLong("height"),
                    address = it.getString("address"),
                    candidateKey = it.optString("candidate_key", null),
                    coin = coin,
                    value = getAmount(it.getString("value")),
                    move_to_candidate_key = it.optString("move_to_candidate_key", null),
                )
                array.add(item)
            }
        }
        return array
    }
}
