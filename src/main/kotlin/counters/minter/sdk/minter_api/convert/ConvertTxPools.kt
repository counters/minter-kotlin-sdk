package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.Coin
import counters.minter.grpc.client.SellAllSwapPoolData
import counters.minter.grpc.client.SellSwapPoolData
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.Enum.TxPool
import counters.minter.sdk.minter.MinterMatch
import org.json.JSONArray
import org.json.JSONObject

class ConvertTxPools {

    companion object : MinterMatch() {
        fun get(data: SellAllSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(data: SellSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(coinsList: List<Coin>, tags: MutableMap<String, String>): List<TxPool> {
            val coinObjMap = mutableMapOf<Long, CoinObjClass.CoinObj>()
            coinsList?.forEach {
                coinObjMap.put(it.id, CoinObjClass.CoinObj(it.id, it.symbol))
            }
            val arrayTxPool = arrayListOf<TxPool>()

            tags["tx.pools"].let {
                JSONArray(it).forEach {
                    it as JSONObject
                    val pool_id = it.getInt("pool_id")
                    val coin_in = it.getLong("coin_in")
                    val value_in = getAmount(it.getString("value_in"))
                    val coin_out = it.getLong("coin_out")
                    val value_out = getAmount(it.getString("value_out"))
                    val txPool = TxPool(pool_id, coinObjMap[coin_in]!!, value_in, coinObjMap[coin_out]!!, value_out)
                    arrayTxPool.add(txPool)
                }
            }
            return arrayTxPool
        }
    }
}