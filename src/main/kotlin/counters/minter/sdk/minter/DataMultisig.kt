package counters.minter.sdk.minter

data class DataMultisig(
    val multisig: String,
    val addresses: MutableMap<String, Long>,
    val threshold: Long,
    val weightsSum: Long,
)