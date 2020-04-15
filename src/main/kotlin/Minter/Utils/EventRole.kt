package Minter.Utils

object EventRole {
    data class Data(val name: String, val uid: Int, val raw: String? = null)

    private val DAO = Data("DAO", 1)
    private val Developers = Data("Developers", 2)
    private val Validator = Data("Validator", 3)
    private val Delegator = Data("Delegator", 4)
    private val NoName = Data("NoName", 0)

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