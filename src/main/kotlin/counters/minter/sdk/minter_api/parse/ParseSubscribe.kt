package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.Minter
import mu.KotlinLogging
import org.joda.time.DateTime
import org.json.JSONObject

class ParseSubscribe {
    private val logger = KotlinLogging.logger {}

    fun status(result: JSONObject): Minter.Status? {
        if (!result.isNull("result")) {

            val header = result.getJSONObject("result").getJSONObject("data").getJSONObject("block").getJSONObject("header")

            val height = header.getLong("height")
            val network = header.getString("chain_id")
            val strTime = header.getString("time")
            val datetime = DateTime(strTime)

            val initialHeight = -1L

            return Minter.Status(
                height,
                datetime,
                network,
                initialHeight,
            )
        } else {
            logger.warn { "Error $result" }
            return null
        }
    }
}
