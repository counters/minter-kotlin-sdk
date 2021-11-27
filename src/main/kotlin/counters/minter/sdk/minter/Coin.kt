package counters.minter.sdk.minter

import counters.minter.sdk.minter.Enum.SwapFromTypes
import org.joda.time.DateTime

class Coin {

    data class EstimateCoin(
        val willGet: Double,
        val commission: Double?,
        val swap_from: SwapFromTypes
    )
    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated? use EstimateCoin",
        replaceWith = ReplaceWith("EstimateCoin")
    )
    data class EstimateCoinSell(
        val willGet: Double,
        val commission: Double,
        val swap_from: SwapFromTypes
    )

    data class EstimateCoinBuy(
        val willPay: Double,
        val commission: Double
    )

    data class CoinPeriod(
        val time: DateTime,
        val coin: Int,
        val avg: Double,
        val min: Double,
        val max: Double,
        val open: Double?,
        val close: Double,
        val volume: Double
    )

}