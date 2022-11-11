package counters.minter.sdk.minter.models

import counters.minter.sdk.minter.CoinObjClass

data class Stake(
    val owner: String,
    val coin: CoinObjClass.CoinObj,
    val value: Double,
    val bipValue: Double,
)
