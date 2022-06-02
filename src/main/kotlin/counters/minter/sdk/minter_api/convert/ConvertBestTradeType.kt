package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.BestTradeRequest
import counters.minter.sdk.minter.models.BestTradeType

class ConvertBestTradeType {

    fun convBestTradeType(type: BestTradeType): BestTradeRequest.Type {
        return when {
            BestTradeType.INPUT == type -> BestTradeRequest.Type.input
            BestTradeType.OUTPUT == type -> BestTradeRequest.Type.output
            else -> BestTradeRequest.Type.UNRECOGNIZED
        }

    }

    fun convBestTradeType(type: BestTradeRequest.Type): BestTradeType {
        return when (BestTradeRequest.Type.input) {
            type -> BestTradeType.INPUT
            type -> BestTradeType.OUTPUT
            else -> BestTradeType.UNRECOGNIZED
        }

    }

}