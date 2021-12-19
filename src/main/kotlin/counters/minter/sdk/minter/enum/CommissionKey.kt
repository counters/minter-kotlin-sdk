package counters.minter.sdk.minter.enum

enum class CommissionKey(val key: String) {
    add_limit_order("add_limit_order"),
    add_liquidity("add_liquidity"),
    burn_token("burn_token"),
    buy_bancor("buy_bancor"),
    buy_pool_base("buy_pool_base"),
    buy_pool_delta("buy_pool_delta"),
//    coin("coin"),
    create_coin("create_coin"),
    create_multisig("create_multisig"),
    create_swap_pool("create_swap_pool"),
    create_ticker3("create_ticker3"),
    create_ticker4("create_ticker4"),
    create_ticker5("create_ticker5"),
    create_ticker6("create_ticker6"),
    create_ticker7_10("create_ticker7_10"),
    create_token("create_token"),
    declare_candidacy("declare_candidacy"),
    delegate("delegate"),
    edit_candidate("edit_candidate"),
    edit_candidate_commission("edit_candidate_commission"),
    edit_candidate_public_key("edit_candidate_public_key"),
    edit_multisig("edit_multisig"),
    edit_ticker_owner("edit_ticker_owner"),
    failed_tx("failed_tx"),
    mint_token("mint_token"),
    multisend_base("multisend_base"),
    multisend_delta("multisend_delta"),
    payload_byte("payload_byte"),
    recreate_coin("recreate_coin"),
    recreate_token("recreate_token"),
    redeem_check("redeem_check"),
    remove_limit_order("remove_limit_order"),
    remove_liquidity("remove_liquidity"),
    sell_all_bancor("sell_all_bancor"),
    sell_all_pool_base("sell_all_pool_base"),
    sell_all_pool_delta("sell_all_pool_delta"),
    sell_bancor("sell_bancor"),
    sell_pool_base("sell_pool_base"),
    sell_pool_delta("sell_pool_delta"),
    send("send"),
    set_candidate_off("set_candidate_off"),
    set_candidate_on("set_candidate_on"),
    set_halt_block("set_halt_block"),
    unbond("unbond"),
    vote_commission("vote_commission"),
    vote_update("vote_update");

    companion object {
        fun fromStr(key: String): CommissionKey? {
            return try {
                valueOf(key)
            } catch (e: Exception) {
                null
            }
        }
    }
}