package counters.minter.sdk.minter.Enum

enum class Subscribe(val str: String) {
    TmEventNewBlock("tm.event = 'NewBlock'"),
    TmEventTx("tm.event = 'Tx'"),
}

