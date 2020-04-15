package Minter


import org.joda.time.DateTime

class Minter {


    data class Block(
        val height: Long?,
        val time: DateTime,
        val num_txs: Int,
        val total_txs: Int,
        var reward: Double,
        var size: Long,
        var proposer: Int?
    )

    data class SignedValidators(val id: Int, val signed: Boolean)

    data class Transaction(
        val id: Long?,
        val hash: String,
        val height: Long,
        val type: Int?,
        val from: Long,
        val to: Long?,
        val node: Int?,
        val stake: String?,
        val coin: Int?,
        val amount: Double?,
        val gas_price: Int,
        val commission: Int?,
        val payload: Boolean,
        val gas: Int,
        val gascoin: Int
    )

    data class Wallet(
        val id: Long?,
        val address: String,
        val count_txs: Int?,
//        val balance: List<MutableMap<String, Double>>
        val balance: MutableMap<String, Double>?
    )

    data class Payload(
        val transaction: Long?,
        val text: String,
        val type: Int
    )

    data class Node(
        val id: Int?,
        val reward: Long,
        val owner: Long,
        val pub_key: String,
        val commission: Int,
        val crblock: Long
    )

    data class Coin(
        val id: Int?,
        val symbol: String,
        val length: Int,
        val name: String,
        val creater: Long?,
        val crr: Int,
        val initrpip: String?,
        val initreserv: Double?,
        val initapip: String?,
        val initamount: Double?,
        val crblock: Long?,
        val enabled: Boolean,
        val numcr: Int
    )

    data class CoinChange(
        val height: Long,
        val transaction: Long?,
        val type: Int,
        val wallet: Long,
        val sell: Double,
        val coins: Int,
        val buy: Double,
        val coinb: Int
    )

    data class CoinPrice(
        val height: Long,
        val coin: Int,
        val sell: Double,
        val buy: Double?
    )

    data class Wallet2Wallet(
        val height: Long,
        val transaction: Long?,
        val from: Long,
        val to: Long,
        val coin: Int,
        val amount: Double
    )

    data class Status(
        val height: Long,
        val datetime: DateTime,
        val network: String
    )

    data class Event(
        val height: Long?,
        val node: Int,
        val wallet: Long,
        val coin: Int?,
        val type: Int,
        val amount: Double,
        var role: Int?
    )
}