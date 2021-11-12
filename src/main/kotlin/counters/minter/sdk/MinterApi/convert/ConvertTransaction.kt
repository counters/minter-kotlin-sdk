package counters.minter.sdk.MinterApi.convert

import com.google.common.io.BaseEncoding.base64
import counters.minter.grpc.client.TransactionResponse
import counters.minter.sdk.Minter.CoinObjClass
import counters.minter.sdk.Minter.MinterRaw

interface ConvertTransaction {

    fun getTransaction(transaction: TransactionResponse): MinterRaw.TransactionRaw {

        return MinterRaw.TransactionRaw(
            hash = transaction.hash,
            height = transaction.height,
            type = transaction.type.toInt(),
            from = transaction.from,
            to = transaction.hash,
            node = transaction.hash,
            stake = "10000000",
            coin = CoinObjClass.CoinObj(-1,"NULL"),
            coin2 = CoinObjClass.CoinObj(-1,"NULL"),
            amount = -1.0,
            gas_price = transaction.gasCoin.id.toInt(),
            commission = -1.0,
            payload = false,
            gas = transaction.gas.toInt(),
            gascoin = CoinObjClass.CoinObj(transaction.gasCoin.id,transaction.gasCoin.symbol),
            optDouble = -1.0,
            optString = "NULL",
            optList = null,
            base64Payload = base64().encode(transaction.payload.toByteArray()) ,

        )
//        val hash: String,
//        val height: Long,
//        val type: Int,
//        val from: String,
//        val to: String?,
//        val node: String?,
//        val stake: String?,
//        val coin: CoinObjClass.CoinObj?,
//        val coin2: CoinObjClass.CoinObj?,
//        val amount: Double?,
//        val gas_price: Int,
//        val commission: Double?,
//        val payload: Boolean,
//        val gas: Int,
//        val gascoin: CoinObjClass.CoinObj,
//        val optDouble: Double?,
//        val optString: String?,
//        val optList: List<Any>?,
//        val base64Payload: String?
    }
}