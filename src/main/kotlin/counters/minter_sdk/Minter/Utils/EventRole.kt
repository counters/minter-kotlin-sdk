package counters.minter_sdk.Minter.Utils

object EventRole {
    data class Data(val name: String, val uid: Int, val raw: String? = null)

    val DAO = Data("DAO", 1)
    val Developers = Data("Developers", 2)
    val Validator = Data("Validator", 3)
    val Delegator = Data("Delegator", 4)
    val NoName = Data("NoName", 0)

    fun get(raw: String):Data {
        return when (raw) {
            "DAO" -> DAO
            "Developers" -> Developers
            "Validator" -> Validator
            "Delegator" -> Delegator
            else -> NoName
        }
    }
    fun get(uid: Int): Data {
        return when (uid) {
            DAO.uid -> DAO
            Developers.uid -> Developers
            Validator.uid -> Validator
            Delegator.uid -> Delegator
            else -> NoName
        }
    }
}