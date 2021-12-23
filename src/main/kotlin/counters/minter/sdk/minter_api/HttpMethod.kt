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
    ESTIMATE_COIN_SELL_ALL("estimate_coin_sell_all"),
    EVENTS("events"),
    TRANSACTION("transaction"),

    LIMIT_ORDER("limit_order"),
    LIMIT_ORDERS("limit_orders"),
}