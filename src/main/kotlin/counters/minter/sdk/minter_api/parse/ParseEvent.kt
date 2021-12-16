package counters.minter.sdk.minter_api.parse

import com.google.protobuf.option
import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.Enum.CommissionKey
import counters.minter.sdk.minter.Models.Commission
import counters.minter.sdk.minter.Utils.EventRole
import counters.minter.sdk.minter.Utils.EventType
import mu.KotlinLogging
import org.json.JSONObject

class ParseEvent: MinterMatch() {

    private var minterMatch = MinterMatch()

    private val mapCache: MutableMap<Long, CoinObjClass.CoinObj> = HashMap()

    private val logger = KotlinLogging.logger {}

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
        var wallet: String? = null
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
                option = it.option,
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
//        var event: counter.sdk.Minter.Event? = null

        if (result.isNull("code")) {

            val array = ArrayList<Minter.Event>()
            val events = result.getJSONArray("events")
            events.forEach {
                val eventJsonObject = it as JSONObject
                val value = eventJsonObject.getJSONObject("value")

                var option: Any? = null


                val node = if (!value.isNull("validator_pub_key")) {
                    getNode(value.getString("validator_pub_key"))
                } else if (!value.isNull("candidate_pub_key")) {
                    getNode(value.getString("candidate_pub_key"))
                } else {
                    null
                }


                val event_type = eventJsonObject.getString("type")
                val type = EventType.get(event_type)
                var role: Int? = null
                if (!value.isNull("role")) {
                    val role_type = value.getString("role")
                    role = EventRole.get(role_type).uid
                }
                var coin: CoinObjClass.CoinObj? = null
//                println(value)
                if (!value.isNull("coin") ) {
                    val coinId = value.getLong("coin")
//                    getCoin(coinId)
                    if (getCoinByIdRaw==null) {
                        coin = CoinObjClass.CoinObj(coinId, null)
                    } else {
//                        val rawCoin = getCoinByIdRaw(coinId)
                        getCoinByIdFromCache(coinId, getCoinByIdRaw).let { coin = it }
                    }
                } else if (!value.isNull("for_coin") ) {
                    val coinId = value.getLong("for_coin")
                    if (getCoinByIdRaw==null) {
                        coin = CoinObjClass.CoinObj(coinId, null)
                    } else {
                        getCoinByIdFromCache(coinId, getCoinByIdRaw).let { coin = it }
                    }
                }

                val wallet = if (!value.isNull("address")) {
                    getWallet(value.getString("address"))
                } else {
                    null
                }
                val amount = if (!value.isNull("amount")) {
                    minterMatch.getAmount(value.getString("amount"))
                } else {
                    null
                }

                if (type == EventType.UpdateCommissions) {
                    val array = arrayListOf<Commission>()
//                    println(value)
                    value.keySet().forEach { key ->
                        CommissionKey.fromStr(key)?.let {
                            val _amount = getAmount(value.getString(key))
                            array.add(Commission(it, _amount))
                        } ?: run {
                            val message = "Error: ${CommissionKey.fromStr(key)}"
                            logger.error { message }
                            throw Exception(message)
                        }
                    }
                    option = listOf(array)
      /*              CommissionKey.values().forEach {

                    }
                    structValue.fieldsMap.forEach { key, value ->
                        val amount = getAmount(value.stringValue)
                        CommissionKey.fromStr(key)?.let {
                            array.add(Commission(it, amount))
                        } ?: run {
                            val message = "Error: ${CommissionKey.fromStr(key)}"
                            logger.error { message }
                            throw Exception(message)
                        }
                        option = listOf(array)
                    }*/
                }

                val event = Minter.Event(
                    height = height,
                    node = node,
                    wallet = wallet,
                    coin = coin,
                    type = type.uid,
                    amount = amount,
                    option = option,
                    role = role
                )
//                println(event)
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