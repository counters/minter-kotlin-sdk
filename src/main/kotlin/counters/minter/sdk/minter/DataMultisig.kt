package counters.minter.sdk.minter

data class DataMultisig(
    val addresses: MutableMap<String, Long>,
    val threshold: Long,
    val weightsSum: Long,
    val multisig: String,
)