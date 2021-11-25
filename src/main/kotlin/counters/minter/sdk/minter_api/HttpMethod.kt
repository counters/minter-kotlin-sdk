package counters.minter.sdk.minter_api

enum class HttpMethod(val patch: String) {
    BLOCK("block"),
    NODE("candidate"),
    ADDRESS("address"),
    COIN("coin_info"),
    COINID("coin_info_by_id"),
    STATUS("status"),
    ESTIMATE_COIN_BUY("estimate_coin_buy"),
    ESTIMATE_COIN_SELL("estimate_coin_sell"),
    EVENTS("events"),
    TRANSACTION("transaction"),
}