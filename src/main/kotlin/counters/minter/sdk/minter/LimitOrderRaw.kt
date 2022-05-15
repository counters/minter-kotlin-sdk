package counters.minter.sdk.minter

data class LimitOrderRaw(
    val id: Long,
    val coinSell: CoinObjClass.CoinObj,
    val wantSell: Double,
    val coinBuy: CoinObjClass.CoinObj,
    val wantBuy: Double,
    val price: Double,
    val owner: String,
    val height: Long,
    val pool_id: Long?
)