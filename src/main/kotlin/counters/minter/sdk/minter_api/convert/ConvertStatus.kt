package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.StatusResponse
import counters.minter.sdk.minter.Minter
import org.joda.time.DateTime

object ConvertStatus {

    fun get(status: StatusResponse): Minter.Status {
        val datetime = DateTime(status.latestBlockTime)
        return Minter.Status(
            height = status.latestBlockHeight,
            datetime = datetime,
            network = status.network,
            initial_height = status.initialHeight,
        )
    }

    fun get(status: Minter.Status): StatusResponse {
//        val blockTime = status.datetime.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))
        val blockTime = status.datetime.toString()
        val statusResponse = StatusResponse.newBuilder()
            .setLatestBlockHeight(status.height)
            .setLatestBlockTime(blockTime)
            .setNetwork(status.network)
            .setInitialHeight(status.initial_height)
        return statusResponse.build()
    }

}