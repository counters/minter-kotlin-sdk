package Minter

import org.joda.time.DateTime
import org.json.JSONObject

class MinterRaw {

    data class NodeRaw(
        val reward: String,
        val owner: String,
        val pub_key: String,
        val commission: Int,
        val crblock: Long
    )

    data class CoinRaw(
        val symbol: String,
        val length: Int,
        val name: String,
        val creater: String,
        val crr: Int,
        val initrpip: String?,
        val initreserv: Double?,
        val initapip: String?,
        val initamount: Double?,
        val crblock: Long?,
        val enabled: Boolean,
        val numcr: Int
    )

    data class TransactionRaw(
        val hash: String,
        val height: Long,
        val type: Int?,
        val from: String,
        val to: String?,
        val node: String,
        val stake: String?,
        val coin: String,
        val amount: Double?,
        val gas_price: Int,
        val commission: Int?,
        val payload: Boolean,
        val gas: Int,
        val gascoin: String
    )

    data class BlockRaw(
        val height: Long?,
        val time: DateTime,
        val num_txs: Int,
        val total_txs: Int,
        var reward: Double,
        var size: Long,
        var proposer: String,
        var transaction: List<TransactionRaw>,
        var transaction_json: List<JSONObject>?
    )
    data class EventRaw(
        val height: Long?,
        val node: String,
        val wallet: String,
        val coin: String?,
        val type: String,
        val amount: Double,
        var role: String?
    )

}
