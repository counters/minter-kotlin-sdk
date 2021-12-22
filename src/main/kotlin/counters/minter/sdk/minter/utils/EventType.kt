package counters.minter.sdk.minter.utils

object EventType {
    data class Data(val name: String, val uid: Int, val raw: String)

    val Reward = Data("Reward", 1,"minter/RewardEvent")
    val Slash = Data("Slash", 2,"minter/SlashEvent")
    val Unbond = Data("Unbond", 3,"minter/UnbondEvent")
    val Jail = Data("Jail", 4,"minter/JailEvent")
    val UpdateCommissions = Data("UpdateCommissions", 5,"minter/UpdateCommissionsEvent")
    val UpdateNetwork = Data("UpdateNetwork", 6,"minter/UpdateNetworkEvent")
    val RemoveCandidate = Data("UpdateNetwork", 7,"minter/RemoveCandidateEvent")
    val StakeKick = Data("StakeKick", 8,"minter/StakeKickEvent")

    val NoName = Data("NoName", 0,"")

    val events = listOf(
        Reward,
        Slash,
        Unbond,
        Jail,
        UpdateCommissions,
        UpdateNetwork,
        RemoveCandidate,
        StakeKick,
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
            else -> NoName
        }
    }
    fun get(uid: Int):Data {
        return when (uid) {
            Reward.uid -> Reward
            Slash.uid -> Slash
            Unbond.uid -> Unbond
            Jail.uid -> Jail
            UpdateCommissions.uid -> UpdateCommissions
            UpdateNetwork.uid -> UpdateNetwork
            RemoveCandidate.uid -> RemoveCandidate
            StakeKick.uid -> StakeKick
            else -> NoName
        }
    }
}