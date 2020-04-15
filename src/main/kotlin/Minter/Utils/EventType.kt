package Minter.Utils

object EventType {
    data class Data(val name: String, val uid: Int, val raw: String)

    private val Reward = Data("Reward", 1,"minter/RewardEvent")
    private val Slash = Data("Slash", 2,"minter/SlashEvent")
    private val Unbond = Data("Unbond", 3,"minter/UnbondEvent")
    private val NoName = Data("NoName", 0,"")

    fun get(raw: String):Data {
        return when (raw) {
            Reward.raw -> Reward
            Slash.raw -> Slash
            Unbond.raw -> Unbond
            else -> NoName
        }
    }
    fun get(uid: Int):Data {
        return when (uid) {
            Reward.uid -> Reward
            Slash.uid -> Slash
            Unbond.uid -> Unbond
            else -> NoName
        }
    }
}