package counters.minter.sdk.minter.models

import counters.minter.sdk.minter.enum.CommissionKey

data class Commission(
    val key: CommissionKey,
    val value: Double
)
