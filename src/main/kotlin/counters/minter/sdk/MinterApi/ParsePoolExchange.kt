package counters.minter.sdk.MinterApi

import counters.minter.sdk.Minter.CoinObjClass
import counters.minter.sdk.Minter.Enum.TxPool
import counters.minter.sdk.Minter.MinterMatch
import counters.minter.sdk.Minter.MinterRaw
import org.json.JSONArray
import org.json.JSONObject

class ParsePoolExchange {

    //    TxPool
    private val minterMatch = MinterMatch()

    /*    data class TxPool (
            val pool_id: Int,
            val coin_in: Long,
            val value_in: Double,
            val coin_out: Long,
            val value_out: Double,
        )*/
    fun get(result: JSONObject): List<MinterRaw.CoinChangeRaw> {

        val height = result.getLong("height")
        val transaction = result.getString("hash")
        val type = result.getInt("type")
        val wallet = result.getString("from")

        val txPools = getTxPools(result)
        return getCoinChangeRaw(txPools, height, transaction, type, wallet)

/*        getTxPools(result)?.forEach {
            val sell = it.value_in
            val coins = it.coin_in
            val buy = it.value_out
            val coinb = it.coin_out
            val pool = it.pool_id
            val coinChangeRaw = MinterRaw.CoinChangeRaw(height, transaction, type, pool, wallet, sell, coins, buy, coinb)
            arrayTxPool.add(coinChangeRaw)
        }
        return arrayTxPool*/
    }

    fun getCoinChangeRaw(coinChangeRaw: List<TxPool>, height: Long, transaction: String, type: Int, wallet: String): List<MinterRaw.CoinChangeRaw>{
        val arrayTxPool = arrayListOf<MinterRaw.CoinChangeRaw>()
        coinChangeRaw.forEach {
            val sell = it.value_in
            val coins = it.coin_in
            val buy = it.value_out
            val coinb = it.coin_out
            val pool = it.pool_id
            val _coinChangeRaw = MinterRaw.CoinChangeRaw(height, transaction, type, pool, wallet, sell, coins, buy, coinb)
            arrayTxPool.add(_coinChangeRaw)
        }
        return arrayTxPool
    }


    fun getTxPools(result: JSONObject): List<TxPool> {

        val tags = result.getJSONObject("tags")
        val data = result.getJSONObject("data")

        val coins = data.getJSONArray("coins")
//        println(coins)
//        val coinObj = arrayListOf<CoinObjClass.CoinObj>()
        val coinObjMap = mutableMapOf<Long, CoinObjClass.CoinObj>()
        coins?.forEach {
            CoinObjClass.fromJson(it as JSONObject)?.let { coin ->
//                coinObj.add(coin)
                coinObjMap.put(coin.id, coin)
            }
        }
//        println(coinObjMap)
        val tx_pools_str = tags.getString("tx.pools")
//        println(tx_pools_str)
        val tx_pools = JSONArray(tx_pools_str)
//        println(tx_pools)

        val arrayTxPool = arrayListOf<TxPool>()

        tx_pools.forEach {
            it as JSONObject
            val pool_id = it.getInt("pool_id")
            val coin_in = it.getLong("coin_in")
            val value_in = minterMatch.getAmount(it.getString("value_in"))
            val coin_out = it.getLong("coin_out")
            val value_out = minterMatch.getAmount(it.getString("value_out"))
            val txPool = TxPool(pool_id, coinObjMap[coin_in]!!, value_in, coinObjMap[coin_out]!!, value_out)
            arrayTxPool.add(txPool)
        }

        return arrayTxPool
    }
}