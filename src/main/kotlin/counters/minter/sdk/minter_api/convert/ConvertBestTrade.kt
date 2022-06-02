package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.BestTradeResponse
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.BestTrade

class ConvertBestTrade : MinterMatch() {
//    var exception: Boolean = true

    fun get(response: BestTradeResponse): BestTrade {
        val path = response.pathList
        val result = getAmount(response.result)
        return BestTrade(
            path = path,
            result = result,
        )
    }
}
