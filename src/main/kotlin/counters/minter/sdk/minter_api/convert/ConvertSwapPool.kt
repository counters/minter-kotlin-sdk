package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.SwapPoolResponse
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw

class ConvertSwapPool : MinterMatch() {
//    private val logger = KotlinLogging.logger {}

    fun get(response: SwapPoolResponse): MinterRaw.SwapPoolRaw {
        val amount0: Double = getAmount(response.amount0)
        val amount1: Double = getAmount(response.amount1)
        val liquidity: Double = getAmount(response.liquidity)

        return MinterRaw.SwapPoolRaw(
            amount0, amount1, liquidity
        )
    }

}
