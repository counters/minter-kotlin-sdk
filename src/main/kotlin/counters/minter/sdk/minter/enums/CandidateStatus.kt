package counters.minter.sdk.minter.enums
enum class CandidateStatus(val key: Int) {
    Status1(1),
    Validator(2),
    Status3(3);

    companion object {
        fun byRaw(raw: Int): CandidateStatus? {
            return when (raw) {
                Status1.key -> Status1
                Validator.key -> Validator
                Status3.key -> Status3
                else -> null
            }
        }
    }
}
