package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.Minter
import org.joda.time.DateTime
import org.json.JSONObject

class ParseStatus {
    fun get(result: JSONObject): Minter.Status? {
//        println(result)
        var status: Minter.Status? = null
        if (result.isNull("error")) {
            val network = result.getString("network")
//            val sync_info = tm_status.getJSONObject("sync_info")
            val latest_block_height = result.getLong("latest_block_height")
            val initial_height = result.getLong("initial_height")
            val latest_block_time = result.getString("latest_block_time")
            val datetime = DateTime(latest_block_time)

            status = Minter.Status(
                latest_block_height,
                datetime,
                network,
                initial_height,
            )
        }
        return status
    }
}