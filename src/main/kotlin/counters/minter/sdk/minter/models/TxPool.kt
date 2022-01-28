package counters.minter.sdk.minter.enum

import counters.minter.sdk.minter.CoinObjClass.CoinObj
import counters.minter.sdk.minter.models.OrderRaw

data class TxPool(
    val pool_id: Int,
    val coin_in: CoinObj,
    val value_in: Double,
    val coin_out: CoinObj,
    val value_out: Double,
    val orders: List<OrderRaw>? = null,
)