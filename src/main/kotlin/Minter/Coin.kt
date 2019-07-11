package Minter

import org.joda.time.DateTime

class Coin {
    data class EstimateCoinSell(
        val willGet: Double,
        val commission: Double
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