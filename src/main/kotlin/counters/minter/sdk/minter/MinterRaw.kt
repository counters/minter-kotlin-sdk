package counters.minter.sdk.minter

import org.joda.time.DateTime
import counters.minter.sdk.minter.CoinObjClass.CoinObj
import counters.minter.sdk.minter.models.TransactionRaw

class MinterRaw {

    @Deprecated("use Candidate()")
    data class NodeRaw(
        val reward: String,
        val owner: String,
        val control: String,
        val pub_key: String,
        val commission: Int,
        val slots: Int,
        val users: Int,
        val min_stake: Double,
        val crblock: Long
    )

    data class CoinRaw(
        val id: Long,
        val symbol: String,
        val name: String,
        val owner: String?,
        val crr: Int,
        val volume: Double,
        val reserve: Double,
        val max_supply: Long?,
        val mintable: Boolean,
        val burnable: Boolean,
    )

    data class SwapPoolRaw(
        val amount0: Double,
        val amount1: Double,
        val liquidity: Double,
    )

    data class CoinChangeRaw(
        val height: Long,
        val transaction: String,
        val type: Int,
        val pool: Int?,
        val wallet: String,
        val sell: Double,
        val coins: CoinObj,
        val buy: Double,
        val coinb: CoinObj
    )

    data class PoolRaw(
        val id: Int,
        val coin0: CoinObj,
        val coin1: CoinObj,
        val volume0: Double,
        val volume1: Double,
        val liquidity: Double,
        val token: CoinObj,
//        val token_symbol: String,
//        val token_id: Long,
    )

    data class MultisendItemRaw(
        val address: String,
        val value: Double,
        val coin: CoinObj
    )

    data class SignedValidatorsRaw(val node: String, val signed: Boolean)


    data class BlockRaw(
        val height: Long?,
        val time: DateTime,
        val num_txs: Int,
//        @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
//        val total_txs: Int,
        var reward: Double?,
        var size: Long,
        var proposer: String,
        var transaction: List<TransactionRaw>,
        var validators: List<SignedValidatorsRaw>,
//        @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
//        var transaction_json: List<JSONObject>?
    )

    data class EventRaw(
        val height: Long?,
        val node: String?,
        val wallet: String?,
        val coin: CoinObj?,
        val type: String,
        val amount: Double?,
        val option: Any?,
        var role: String?

  /*      "type": "minter/UpdateNetworkEvent",
    "value": {
        "version": "v260"
    }*/
    )

}
