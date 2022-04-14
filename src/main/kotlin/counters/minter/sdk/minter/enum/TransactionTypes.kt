package counters.minter.sdk.minter.enum

/**
 * https://github.com/MinterTeam/minter-go-node/blob/master/coreV2/transaction/transaction.go
 */
enum class TransactionTypes (val int: Int) {
    TypeSend(1),
    TypeSellCoin(2),
    TypeSellAllCoin(3),
    TypeBuyCoin(4),
    TypeCreateCoin(5),
    TypeDeclareCandidacy(6),
    TypeDelegate(7),
    TypeUnbond(8),
    TypeRedeemCheck(9),
    TypeSetCandidateOnline(10),
    TypeSetCandidateOffline(11),
    TypeCreateMultisig(12),
    TypeMultiSend(13),
    TypeEditCandidate(14),
    TypeSetHaltBlock(15),
    TypeRecreateCoin(16),
    TypeEditCoinOwner(17),
    TypeEditMultisig(18),
    TypePriceVote(19),
    TypeEditCandidatePublicKey(20),
    ADD_LIQUIDITY(21),
    REMOVE_LIQUIDITY(22),
    SELL_SWAP_POOL(23),
    BUY_SWAP_POOL(24),
    SELL_ALL_SWAP_POOL(25),
    EDIT_CANDIDATE_COMMISSION(26),
    MOVE_STAKE(27),
    MINT_TOKEN(28),
    BURN_TOKEN(29),
    CREATE_TOKEN(30),
    RECREATE_TOKEN(31),
    VOTE_COMMISSION(32),
    VOTE_UPDATE(33),
    CREATE_SWAP_POOL(34),
    ADD_LIMIT_ORDER(35),
    REMOVE_LIMIT_ORDER(36),
    LOCK_STAKE(37),
    LOCK(38),
    ;

    fun toHex(): String {
        this.int.toString(16).let {
            if (it.length==1) return "0$it"
            else return it
        }
    }
}

