package counters.minter.sdk.minter.utils

object EventType {
    data class Data(val name: String, val uid: Int, val raw: String)

    val Reward = Data("Reward", 1, "minter/RewardEvent")
    val Slash = Data("Slash", 2, "minter/SlashEvent")
    val Unbond = Data("Unbond", 3, "minter/UnbondEvent")
    val Jail = Data("Jail", 4, "minter/JailEvent")
    val UpdateCommissions = Data("UpdateCommissions", 5, "minter/UpdateCommissionsEvent")
    val UpdateNetwork = Data("UpdateNetwork", 6, "minter/UpdateNetworkEvent")
    val RemoveCandidate = Data("RemoveCandidate", 7, "minter/RemoveCandidateEvent")
    val StakeKick = Data("StakeKick", 8, "minter/StakeKickEvent")
    val OrderExpired = Data("OrderExpired", 9, "minter/OrderExpiredEvent")
    val UnlockEvent = Data("UnlockEvent", 10, "minter/UnlockEvent") // add in v3.0.0
    val UpdatedBlockRewardEvent = Data("UpdatedBlockRewardEvent", 11, "minter/UpdatedBlockRewardEvent") // add in v3.0.0
    val StakeMoveEvent = Data("StakeMoveEvent", 12, "minter/StakeMoveEvent") // add in v3.0.0

    val NoName = Data("NoName", 0, "")

    val events = listOf(
        Reward,
        Slash,
        Unbond,
        Jail,
        UpdateCommissions,
        UpdateNetwork,
        RemoveCandidate,
        StakeKick,
        OrderExpired,
        UnlockEvent,
        UpdatedBlockRewardEvent,
        StakeMoveEvent,
    )

    fun get(raw: String): Data {
        return when (raw) {
            Reward.raw -> Reward
            Slash.raw -> Slash
            Unbond.raw -> Unbond
            Jail.raw -> Jail
            UpdateCommissions.raw -> UpdateCommissions
            UpdateNetwork.raw -> UpdateNetwork
            RemoveCandidate.raw -> RemoveCandidate
            StakeKick.raw -> StakeKick
            OrderExpired.raw -> OrderExpired
            UnlockEvent.raw -> UnlockEvent
            UpdatedBlockRewardEvent.raw -> UpdatedBlockRewardEvent
            StakeMoveEvent.raw -> StakeMoveEvent
            else -> NoName
        }
    }

    fun get(uid: Int): Data {
        return when (uid) {
            Reward.uid -> Reward
            Slash.uid -> Slash
            Unbond.uid -> Unbond
            Jail.uid -> Jail
            UpdateCommissions.uid -> UpdateCommissions
            UpdateNetwork.uid -> UpdateNetwork
            RemoveCandidate.uid -> RemoveCandidate
            StakeKick.uid -> StakeKick
            OrderExpired.uid -> OrderExpired
            UnlockEvent.uid -> UnlockEvent
            UpdatedBlockRewardEvent.uid -> UpdatedBlockRewardEvent
            StakeMoveEvent.uid -> StakeMoveEvent
            else -> NoName
        }
    }
}