package counters.minter.sdk.minter.models

import counters.minter.sdk.minter.CoinObjClass

data class FrozenAllRaw(
    val height: Long,
    val address: String,
    val candidateKey: String?,
    val coin: CoinObjClass.CoinObj,
    val value: Double,
    val move_to_candidate_key: String?,
)
