package MinterApi

import Minter.*
import Minter.Utils.EventRole
import Minter.Utils.EventType
import org.json.JSONObject

class ParseEvent {
    var minterMatch = MinterMatch()

    //    defaultCoin
    fun getRaw(result: JSONObject, height: Long): List<MinterRaw.EventRaw>? {
        var coin: String = ""
        var wallet: String = ""
        var node: String = ""


        val eventList = get(result, height, {
            coin = it
            0 // Coin
        }, {
            wallet = it
            0L //getWallet
        }, {
            node = it
            0 // getNode
        }
        )

        if (eventList != null) {
            val array = ArrayList<MinterRaw.EventRaw>()
            eventList.forEach {
//                val roleObj = EventRole.get(it.role)
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
        }
        return null
    }

    fun get(
        result: JSONObject,
        height: Long,
        getCoin: ((symbol: String) -> Int),
        getWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
//        getType: ((address: String) -> Int),
        getOther: ((jsonObject: JSONObject) -> Unit)? = null
    ): List<Minter.Event>? {
//        println(result)
//        var event: Minter.Event? = null

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
                var coin: Int? = null
                if (!value.isNull("coin")) {
                    coin = getCoin(value.getString("coin"))
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
            }

//            println(array)
            return array
        }
        return null
    }

    fun getCoin(symbol: String, getCoin: ((symbol: String) -> Int)): Int {
        if (symbol != Conf.defaultCoin)
            return getCoin(symbol)
        return Conf.defaultCoinUid
    }
}