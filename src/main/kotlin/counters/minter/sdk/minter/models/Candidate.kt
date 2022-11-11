package counters.minter.sdk.minter.models

import counters.minter.sdk.minter.enums.CandidateStatus

data class Candidate (
    val reward: String,
    val owner: String,
    val control: String,
    val totalStake: Double,
    val publicKey: String,
    val commission: Int,
    val slots: Int,
    val users: Int,
    val minStake: Double,
    val stakes: List<Stake>,
    val status: CandidateStatus,
    val validator: Boolean,
    val jailedUntil: Long
)
