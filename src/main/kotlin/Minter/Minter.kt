package Minter


import org.joda.time.DateTime
import Minter.CoinObjClass.CoinObj

class Minter {

    data class Block(
        val height: Long?,
        val time: DateTime,
        val num_txs: Int,
        val total_txs: Int,
        var reward: Double,
        var size: Long,
        var proposer: Int?
//        var transaction: List<Transaction>?,
//        var validators: List<SignedValidators>?
    )

    data class SignedValidators(val id: Int, val signed: Boolean)

    data class Transaction(
        val id: Long?,
        val hash: String,
        val height: Long,
        val type: Int,
        val from: Long,
        val to: Long?,
        val node: Int?,
        val stake: String?,
        val coin: CoinObj?,
        val coin2: CoinObj?,
        val amount: Double?,
        val gas_price: Int,
        val commission: Int?,
        val payload: Boolean,
        val gas: Int,
        val gascoin: CoinObj,
        val optDouble: Double?,
        val optString: String?
    )

    data class MultisendItem(
        val address: String,
        val coin: CoinObj,
        val value: Double
    )

    data class Wallet(
        val id: Long?,
        val address: String,
        val count_txs: Long,
        val balance: List<Balance>,
        val delegated: List<Balance>?,
        val total: List<Balance>?,
        val bip_value: Double
    )

    data class Balance(
        val coin: CoinObj,
        val value: Double,
        val bipValue: Double
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
        val control: Long,
        val pub_key: String,
        val commission: Int,
        val slots: Int,
        val users: Int,
        val min_stake: Double,
        val crblock: Long
    )

    data class Coin(
        val id: Int,
        val symbol: String,
        val length: Int,
        val name: String,
        val creater: Long?,
        val owner: Long?,
        val crr: Int,
        val volume: Double,
        val reserve: Double,
        val max_supply: Long?,
        val initrpip: String?,
        val initreserv: Double?,
        val initapip: String?,
        val initamount: Double?,
        val crblock: Long?,
        val enabled: Boolean
    )
    
    data class CoinCurr(
        val coin: Int?,
        val volume: Double,
        val reserve: Double,
        val sell: Double,
        val height: Long?
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
        val coin: CoinObj?,
        val type: Int,
        val amount: Double,
        var role: Int?
    )
}