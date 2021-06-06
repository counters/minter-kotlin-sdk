package counters.minter.sdk.Minter.Enum

import counters.minter.sdk.Minter.CoinObjClass.CoinObj

data class TxPool (
    val pool_id: Int,
    val coin_in: CoinObj,
    val value_in: Double,
    val coin_out: CoinObj,
    val value_out: Double,
)
data class _TxPool (
    val pool_id: Int,
    val coin_in: Long,
    val value_in: Double,
    val coin_out: Long,
    val value_out: Double,
)