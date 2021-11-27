package counters.minter.sdk.minter

@Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated? use *.minter.Enum.TransactionTypes")
object TransactionTypes {

    const val TypeSend = 1 // 7, 509
    const val TypeSellCoin = 2 // 9
    const val TypeSellAllCoin = 3 // 4, 7
    const val TypeBuyCoin = 4 // 4
    const val TypeCreateCoin = 5 // 5, 6
    const val TypeDeclareCandidacy = 6 // 5
    const val TypeDelegate = 7 // 509
    const val TypeUnbond = 8
    const val TypeRedeemCheck = 9
    const val TypeSetCandidateOnline = 10 //8
    const val TypeSetCandidateOffline = 11
    const val TypeCreateMultisig = 12
    const val TypeMultiSend = 13 // 2
    const val TypeEditCandidate = 14
    const val TypeSetHaltBlock = 15
    const val TypeRecreateCoin = 16
    const val TypeEditCoinOwner = 17
    const val TypeEditMultisig = 18
    const val TypePriceVote = 19
    const val TypeEditCandidatePublicKey = 20

}