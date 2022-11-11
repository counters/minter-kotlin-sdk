package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.enums.TxPool
import counters.minter.sdk.minter.models.OrderRaw
import org.json.JSONArray
import org.json.JSONObject

class ParsePoolExchange {

    @Deprecated("")
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject): List<MinterRaw.CoinChangeRaw> {

        val height = result.getLong("height")
        val transaction = result.getString("hash")
        val type = result.getInt("type")
        val wallet = result.getString("from")

        val txPools = getTxPools(result)
        return getCoinChangeRaw(txPools, height, transaction, type, wallet)
    }

    fun getCoinChangeRaw(listTxPool: List<TxPool>, height: Long, transaction: String, type: Int, wallet: String): List<MinterRaw.CoinChangeRaw> {
        val arrayTxPool = arrayListOf<MinterRaw.CoinChangeRaw>()
        listTxPool.forEach {
            val sell = it.value_in
            val coins = it.coin_in
            val buy = it.value_out
            val coinb = it.coin_out
            val pool = it.pool_id
            val coinChangeRaw =
                MinterRaw.CoinChangeRaw(height, transaction, type, pool, wallet, sell, coins, buy, coinb)
            arrayTxPool.add(coinChangeRaw)
        }
        return arrayTxPool
    }


    fun getTxPools(result: JSONObject): List<TxPool> {

        val tags = result.getJSONObject("tags")
        val data = result.getJSONObject("data")

        val coins = data.getJSONArray("coins")

        val coinObjMap = mutableMapOf<Long, CoinObjClass.CoinObj>()
        coins?.forEach {
            CoinObjClass.fromJson(it as JSONObject)?.let { coin ->
                coinObjMap.put(coin.id, coin)
            }
        }
        val tx_pools_str = tags.getString("tx.pools")
        val tx_pools = JSONArray(tx_pools_str)
        val arrayTxPool = arrayListOf<TxPool>()

        tx_pools.forEach {
            it as JSONObject
            val pool_id = it.getInt("pool_id")
            val coin_in = it.getLong("coin_in")
            val value_in = minterMatch.getAmount(it.getString("value_in"))
            val coin_out = it.getLong("coin_out")
            val value_out = minterMatch.getAmount(it.getString("value_out"))

            var arrayOrderRaw: ArrayList<OrderRaw>? = null
            if (!it.isNull("details")) {
                it.getJSONObject("details").let { details ->
                    if (!details.isNull("orders")) {
                        arrayOrderRaw = arrayListOf()
                        details.getJSONArray("orders").forEach { order ->
                            order as JSONObject
                            val orderRaw = OrderRaw(
                                id = order.getLong("id"),
                                buy = minterMatch.getAmount(order.getString("buy")),
                                sell = minterMatch.getAmount(order.getString("sell")),
                                seller = order.getString("seller"),
                            )
                            arrayOrderRaw!!.add(orderRaw)
                        }
                    }
                }
            }

            val txPool = TxPool(
                pool_id = pool_id, coin_in = coinObjMap[coin_in]!!, value_in = value_in,
                coin_out = coinObjMap[coin_out]!!,
                value_out = value_out,
                orders = arrayOrderRaw
            )
            arrayTxPool.add(txPool)
        }

        return arrayTxPool
    }
}
