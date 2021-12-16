package counters.minter.sdk.minter_api

import org.json.JSONException
import org.json.JSONObject

interface StringJSON {
    fun getJSONObject(strJson: String?): JSONObject? {
        if (strJson == null) return null
        return try {
            JSONObject(strJson)
        } catch (e: JSONException) {
//            logger.error { "JSONException $e" }
            println("JSONException $e")
            null
        }
    }
}