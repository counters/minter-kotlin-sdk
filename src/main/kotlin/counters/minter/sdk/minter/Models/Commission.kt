package counters.minter.sdk.minter.Models

import counters.minter.sdk.minter.Enum.CommissionKey

data class Commission(
    val key: CommissionKey,
    val value: Double
)
