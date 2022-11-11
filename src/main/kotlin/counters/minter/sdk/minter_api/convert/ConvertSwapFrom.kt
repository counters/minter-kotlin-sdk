package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.SwapFrom
import counters.minter.sdk.minter.enums.SwapFromTypes

class ConvertSwapFrom {
    fun convSwapFrom(swap_from: SwapFromTypes): SwapFrom {
        return SwapFrom.valueOf(swap_from.value)
    }

    fun convSwapFrom(swap_from: SwapFrom): SwapFromTypes {
        SwapFromTypes.values().firstOrNull { it.value==swap_from.name }?.let {
            return it
        } ?: run {
            return SwapFromTypes.UNRECOGNIZED
        }

    }

}
