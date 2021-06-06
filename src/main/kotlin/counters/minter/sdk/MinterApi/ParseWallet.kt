package counters.minter.sdk.MinterApi

import counters.minter.sdk.Minter.CoinObjClass
import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterMatch
import org.json.JSONArray
import org.json.JSONObject

class ParseWallet {
    var minterMatch = MinterMatch()
    fun get(result: JSONObject, address: String): Minter.Wallet? {
//        val balance = mutableMapOf<String, Double>()
        val balance = mutableMapOf<CoinObjClass.CoinObj, Double>()
        val transaction_count = result.getLong("transaction_count")
        val bip_value = result.getString("bip_value")

        val arrayBalance = if (result.isNull("balance")) null else getListBalance(result.getJSONArray("balance"))
        val arrayDelegated = if (result.isNull("delegated")) null else getListBalance(result.getJSONArray("delegated"))
        val arrayTotal = if (result.isNull("total")) null else getListBalance(result.getJSONArray("total"))
       /* balanceJSONArray.names().forEach {
            val key = it as String
            val amount = minterMatch.getAmount(balanceJSONObject.get(key) as String)
            balance.put(key, amount)
        }*/
//        return null
        if (arrayBalance != null && arrayTotal != null)
            return Minter.Wallet(null, address, transaction_count, arrayBalance, arrayDelegated, arrayTotal, minterMatch.getAmount(bip_value))
        else
            return null

    }
    private fun getListBalance(jsonArray: JSONArray): List<Minter.Balance> {
        val arrayBalance = ArrayList<Minter.Balance>()
        jsonArray.forEach {
            it as JSONObject
            val coin = CoinObjClass.fromJson(it.getJSONObject("coin") )
            val value = minterMatch.getAmount(it.getString("value"))
            val bipValue = minterMatch.getAmount(it.getString("bip_value"))
            arrayBalance.add(Minter.Balance(coin!!, value, bipValue))
        }
        return arrayBalance
    }
}

