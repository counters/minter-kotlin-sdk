package counters.minter.sdk.minter_api.convert

import com.google.common.io.BaseEncoding.base64
import counters.minter.grpc.client.TransactionResponse
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.models.TransactionRaw

@Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
interface ConvertTransactionOld {

    fun getTransaction(transaction: TransactionResponse): TransactionRaw {

        return TransactionRaw(
            hash = transaction.hash,
            height = transaction.height,
            type = transaction.type.toInt(),
            from = transaction.from,
            to = transaction.hash,
            node = transaction.hash,
            stake = "10000000",
            coin = CoinObjClass.CoinObj(-1, "NULL"),
            coin2 = CoinObjClass.CoinObj(-1, "NULL"),
            amount = -1.0,
            gasPrice = transaction.gasCoin.id.toInt(),
            commission = -1.0,
//            commissionCoinId = -1,
            payload = false,
            gas = transaction.gas.toInt(),
            gasCoin = CoinObjClass.CoinObj(transaction.gasCoin.id, transaction.gasCoin.symbol),
            optDouble = -1.0,
            optString = "NULL",
            optData = null,
            base64Payload = base64().encode(transaction.payload.toByteArray()),
            code = transaction.code.toInt()
        )
    }
}