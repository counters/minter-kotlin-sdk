package counters.minter.sdk.minter.models

data class DataEditCandidate(
    val pub_key: String,
    val reward_address: String,
    val owner_address: String,
    val control_address: String,
)
