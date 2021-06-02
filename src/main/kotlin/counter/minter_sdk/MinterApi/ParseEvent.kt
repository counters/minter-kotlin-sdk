package counter.minter_sdk.MinterApi

import counter.minter_sdk.Minter.*
import counter.minter_sdk.Minter.Utils.EventRole
import counter.minter_sdk.Minter.Utils.EventType
import org.json.JSONObject

class ParseEvent {
    var minterMatch = MinterMatch()

    private val mapCache: MutableMap<Long, CoinObjClass.CoinObj> = HashMap()

    private fun getCoinByIdFromCache(coinId: Long, getCoinByIdRaw: ((id: Long) -> MinterRaw.CoinRaw?)): CoinObjClass.CoinObj {
        if (coinId == Conf.defaultCoinUid) return CoinObjClass.CoinObj(Conf.defaultCoinUid, Conf.defaultCoin)
        if (mapCache.contains(coinId)) {
//            println("FromCache({mapCache[coinId]})")
            return mapCache[coinId]!!
        } else {
            getCoinByIdRaw(coinId)?.let {
                val coinObj = CoinObjClass.CoinObj(it.id, it.symbol)
                mapCache[coinId] = coinObj
//                println("getCoinByIdRaw($coinId)")
                return coinObj
            }
        }
        return CoinObjClass.CoinObj(coinId, null)
    }
    /**
     * get raw events
     */
    fun getRaw(result: JSONObject,
               height: Long,
               getCoinByIdRaw: ((id: Long) -> MinterRaw.CoinRaw?)? = null
    ): List<MinterRaw.EventRaw>? {
//        var coin: String? = null
        var wallet: String = ""
        var node: String = ""

        val array = ArrayList<MinterRaw.EventRaw>()


        val eventList = get(result, height,/* {
            coin = it
            0 // Coin
        }, */{
            wallet = it
            0L //getWallet
        }, {
            node = it
            0 // getNode
        }, null, {
            var role: String? = null
            if (it.role != null) {
                role = EventRole.get(it.role!!).name
            }
            val eventRaw = MinterRaw.EventRaw(
                height = height,
                node = node,
                wallet = wallet,
                coin = it.coin,
                type = EventType.get(it.type).name,
                amount = it.amount,
                role = role
            )
            array.add(eventRaw)
        }, getCoinByIdRaw )

/*        if (eventList != null) {
            val array = ArrayList<MinterRaw.EventRaw>()
            eventList.forEach {
                var role: String? = null
                if (it.role!=null){
                    role = EventRole.get(it.role!!).name
                }
                val eventRaw = MinterRaw.EventRaw(
                    height = height,
                    node = node,
                    wallet = wallet,
                    coin = coin,
                    type = EventType.get(it.type).name,
                    amount = it.amount,
                    role = role
                )
                array.add(eventRaw)
            }
            return array
        }*/

        return if (eventList == null)
            null
        else array
    }

    fun get(
        result: JSONObject,
        height: Long,
//        getCoin: ((symbol: String) -> Int),
        getWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
        getOther: ((jsonObject: JSONObject) -> Unit)? = null,
        success: ((event: Minter.Event) -> Unit)? = null,
        getCoinByIdRaw: ((id: Long) -> MinterRaw.CoinRaw?)? = null
    ): List<Minter.Event>? {
//        println(result)
//        var event: counter.minter_sdk.Minter.Event? = null

        if (result.isNull("code")) {

            val array = ArrayList<Minter.Event>()
            val events = result.getJSONArray("events")
            events.forEach {
                val eventJsonObject = it as JSONObject
                val value = eventJsonObject.getJSONObject("value")

                val node = getNode(value.getString("validator_pub_key"))
                val event_type = eventJsonObject.getString("type")
                val type = EventType.get(event_type).uid
                var role: Int? = null
                if (!value.isNull("role")) {
                    val role_type = value.getString("role")
                    role = EventRole.get(role_type).uid
                }
                var coin: CoinObjClass.CoinObj? = null
                if (!value.isNull("coin")) {
                    val coinId = value.getLong("coin")
//                    getCoin(coinId)
                    if (getCoinByIdRaw==null) {
                        coin = CoinObjClass.CoinObj(coinId, null)
                    } else {
//                        val rawCoin = getCoinByIdRaw(coinId)
                        getCoinByIdFromCache(coinId, getCoinByIdRaw).let { coin = it }
                    }
                }
                val event = Minter.Event(
                    height = height,
                    node = node,
                    wallet = getWallet(value.getString("address")),
                    coin = coin,
                    type = type,
                    amount = minterMatch.getAmount(value.getString("amount")),
                    role = role
                )
                array.add(event)
                getOther?.invoke(eventJsonObject)
                success?.invoke(event)
            }

//            println(array)
            mapCache.clear()
            return array
        }
        mapCache.clear()
        return null
    }

/*    fun getCoin(symbol: String, getCoin: ((symbol: String) -> Int)): Long {
        if (symbol != Conf.defaultCoin)
            return getCoin(symbol)
        return Conf.defaultCoinUid
    }*/
}