package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.Coin
import counters.minter.grpc.client.SellAllSwapPoolData
import counters.minter.grpc.client.SellSwapPoolData
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.enum.TxPool
import counters.minter.sdk.minter.models.OrderRaw
import org.json.JSONArray
import org.json.JSONObject

class ConvertTxPools {

    companion object : MinterMatch() {
        fun get(data: SellAllSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(data: SellSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(coinsList: List<Coin>, tags: MutableMap<String, String>): List<TxPool> {
            val coinObjMap = mutableMapOf<Long, CoinObjClass.CoinObj>()
            coinsList.forEach {
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

                    var arrayOrderRaw: ArrayList<OrderRaw>? = null

                    if (!it.isNull("details")) {
                        it.getJSONObject("details").let { details ->
                            if (!details.isNull("orders")) {
                                arrayOrderRaw = arrayListOf()
                                details.getJSONArray("orders").forEach { order ->
                                    order as JSONObject
                                    val orderRaw = OrderRaw(
                                        id = order.getLong("id"),
                                        buy = getAmount(order.getString("buy")),
                                        sell = getAmount(order.getString("sell")),
                                        seller = order.getString("seller"),
                                    )
                                    arrayOrderRaw!!.add(orderRaw)
                                }
                            }
                        }
                    }
                    val txPool = TxPool(
                        pool_id = pool_id,
                        coin_in = coinObjMap[coin_in]!!,
                        value_in = value_in,
                        coin_out = coinObjMap[coin_out]!!,
                        value_out = value_out,
                        orders = arrayOrderRaw
                    )
                    arrayTxPool.add(txPool)
                }
            }
            return arrayTxPool
        }
    }

}