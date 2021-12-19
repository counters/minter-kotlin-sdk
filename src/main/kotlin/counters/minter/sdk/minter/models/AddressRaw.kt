package counters.minter.sdk.minter.models

import counters.minter.sdk.minter.DataMultisig
import counters.minter.sdk.minter.Minter

data class AddressRaw(
    val address: String,
    val count_txs: Long,
    val balance: List<Minter.Balance>,
    val delegated: List<Minter.Delegated>?,
    val total: List<Minter.Balance>?,
    val bip_value: Double,
    val multisig: DataMultisig?
)
