package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.SubscribeResponse
import counters.minter.sdk.minter.Minter
import mu.KotlinLogging
import org.joda.time.DateTime

object ConvertSubscribe {

    private val logger = KotlinLogging.logger {}

    fun status(subscribeResponse: SubscribeResponse): Minter.Status? {
//        logger.info { subscribeResponse }
        val initial_height = -1L

//        val query = subscribeResponse.query
        val block = subscribeResponse.data.getFieldsOrThrow("block")
        val header = block.structValue.getFieldsOrThrow("header")
        val height = header.structValue.getFieldsOrThrow("height").numberValue.toLong()
        val network = header.structValue.getFieldsOrThrow("chain_id").stringValue
        val strTime = header.structValue.getFieldsOrThrow("time").stringValue
        val datetime= DateTime(strTime)

        return Minter.Status(
            height,
            datetime,
            network,
            initial_height,
        )
    }
/*    data class Status(
        val height: Long,
        val datetime: DateTime,
        val network: String,
        val initial_height: Long,
    )*/
}
