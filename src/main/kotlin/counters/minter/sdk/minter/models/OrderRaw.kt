package counters.minter.sdk.minter.models

data class OrderRaw(
    val id: Long,
    val buy: Double,
    val sell: Double,
    val seller: String,
)