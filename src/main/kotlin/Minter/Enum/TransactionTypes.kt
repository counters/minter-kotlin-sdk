package Minter.Enum

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
    TypeEditCandidatePublicKey(20)
}
