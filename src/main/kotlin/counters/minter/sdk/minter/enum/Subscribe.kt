package counters.minter.sdk.minter.enum

enum class Subscribe(val str: String) {
    TmEventNewBlock("tm.event = 'NewBlock'"),
    TmEventTx("tm.event = 'Tx'"),
}

