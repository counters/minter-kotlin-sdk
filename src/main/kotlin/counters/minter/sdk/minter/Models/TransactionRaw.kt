package counters.minter.sdk.minter.Models

import counters.minter.sdk.minter.CoinObjClass

data class TransactionRaw(
    val hash: String,
    val height: Long,
    val type: Int,
    val from: String,
    val to: String?,
    val node: String?,
    val stake: String?,
    val coin: CoinObjClass.CoinObj?,
    val coin2: CoinObjClass.CoinObj?,
    val amount: Double?,
    val gasPrice: Int,
    val commission: Double?,
//        val commissionCoinId: Long?,
    val payload: Boolean,
    val gas: Int,
    val gasCoin: CoinObjClass.CoinObj,
    val optDouble: Double?,
    val optString: String?,
//    val optList: List<Any>?,
    val optData: Any?,
    val base64Payload: String?,
    val code: Int
)