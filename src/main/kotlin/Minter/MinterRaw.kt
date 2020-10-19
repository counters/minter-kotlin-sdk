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
        val crr: Int,
        val volume: Double,
        val reserve_balance: Double,
        val max_supply: Double
    )

    data class TransactionRaw(
        val hash: String,
        val height: Long,
        val type: Int?,
        val from: String,
        val to: String?,
        val node: String?,
        val stake: String?,
        val coin: String?,
        val coin2: String?,
        val amount: Double?,
        val gas_price: Int,
        val commission: Int?,
        val payload: Boolean,
        val gas: Int,
        val gascoin: String
    )

    data class SignedValidatorsRaw(val node: String, val signed: Boolean)


    data class BlockRaw(
        val height: Long?,
        val time: DateTime,
        val num_txs: Int,
        val total_txs: Int,
        var reward: Double,
        var size: Long,
        var proposer: String,
        var transaction: List<TransactionRaw>,
        var validators: List<SignedValidatorsRaw>,
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
