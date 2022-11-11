package counters.minter.sdk.minter.enums

enum class Subscribe(val str: String) {
    TmEventNewBlock("tm.event = 'NewBlock'"),
    TmEventTx("tm.event = 'Tx'"),
}

