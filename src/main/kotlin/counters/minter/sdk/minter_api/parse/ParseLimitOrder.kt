package counters.minter.sdk.minter_api.parse

import org.json.JSONObject

class ParseLimitOrder {

    fun get(result: JSONObject): Any? {
//        println("result $result")
        return null
    }

    fun array(result: JSONObject): List<Any>? {
//        println("result $result")
        result.optJSONArray("orders")?.let {
//            println("orders $it")
            val array = arrayListOf<Any>()
            it.forEach {
                get(it as JSONObject)?.let {
                    array.add(it)
                }
            }
            return array
        }
        return null
    }

}