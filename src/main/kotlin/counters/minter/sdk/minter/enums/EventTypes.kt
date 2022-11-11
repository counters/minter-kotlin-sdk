package counters.minter.sdk.minter.enums

import counters.minter.sdk.minter.models.EventData
import counters.minter.sdk.minter.utils.EventType

enum class EventTypes(val data: EventData) {

    Reward(EventData("Reward", 1, "minter/RewardEvent")),
    Slash(EventData("Slash", 2, "minter/SlashEvent")),
    Unbond(EventData("Unbond", 3, "minter/UnbondEvent")),
    Jail(EventData("Jail", 4, "minter/JailEvent")),
    UpdateCommissions(EventData("UpdateCommissions", 5, "minter/UpdateCommissionsEvent")),
    UpdateNetwork(EventData("UpdateNetwork", 6, "minter/UpdateNetworkEvent")),
    RemoveCandidate(EventData("RemoveCandidate", 7, "minter/RemoveCandidateEvent")),
    StakeKick(EventData("StakeKick", 8, "minter/StakeKickEvent")),
    OrderExpired(EventData("OrderExpired", 9, "minter/OrderExpiredEvent")),
    Unlock(EventData("Unlock", 10, "minter/UnlockEvent")),
    UpdatedBlockReward(EventData("UpdatedBlockReward", 11, "minter/UpdatedBlockRewardEvent")),
    StakeMove(EventData("StakeMove", 12, "minter/StakeMoveEvent")),

    NoName(EventData("NoName", 0, ""))
    ;

    fun eq(int: Int) = this.data.uid == int
    fun eq(name: String) = this.data.name == name

    @Deprecated("")
    fun toOldType(): EventType.Data {
        return EventType.get(this.data.raw)
    }

    companion object {
        fun byRaw(raw: String): EventTypes {
            return when (raw) {
                Reward.data.raw -> Reward
                Slash.data.raw -> Slash
                Unbond.data.raw -> Unbond
                Jail.data.raw -> Jail
                UpdateCommissions.data.raw -> UpdateCommissions
                UpdateNetwork.data.raw -> UpdateNetwork
                RemoveCandidate.data.raw -> RemoveCandidate
                StakeKick.data.raw -> StakeKick
                OrderExpired.data.raw -> OrderExpired
                Unlock.data.raw -> Unlock
                UpdatedBlockReward.data.raw -> UpdatedBlockReward
                StakeMove.data.raw -> StakeMove
                else -> NoName
            }
        }
    }

    @Deprecated("use EventTypes.byRaw(minter/StakeMoveEvent)", ReplaceWith("byRaw(raw)", "counters.minter.sdk.minter.enum.EventTypes"))
    fun get(raw: String) = byRaw(raw)

    @Deprecated("")
    fun get(uid: Int): EventTypes {
        return when (uid) {
            Reward.data.uid -> Reward
            Slash.data.uid -> Slash
            Unbond.data.uid -> Unbond
            Jail.data.uid -> Jail
            UpdateCommissions.data.uid -> UpdateCommissions
            UpdateNetwork.data.uid -> UpdateNetwork
            RemoveCandidate.data.uid -> RemoveCandidate
            StakeKick.data.uid -> StakeKick
            OrderExpired.data.uid -> OrderExpired
            Unlock.data.uid -> Unlock
            UpdatedBlockReward.data.uid -> UpdatedBlockReward
            StakeMove.data.uid -> StakeMove
            else -> NoName
        }
    }
}
