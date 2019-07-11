package MinterApi

import Minter.Minter
import Minter.MinterMatch
import org.json.JSONObject

class ParseWallet {
    var minterMatch = MinterMatch()
    fun get(result: JSONObject, address: String): Minter.Wallet? {
        val balance = mutableMapOf<String, Double>()

        val transaction_count = result.getInt("transaction_count")
        val balanceJSONObject = result.getJSONObject("balance") as JSONObject
        balanceJSONObject.names().forEach {
            val key = it as String
            val amount = minterMatch.getAmount(balanceJSONObject.get(key) as String)
            balance.put(key, amount)
        }
        val wallet = Minter.Wallet(null, address, transaction_count, balance)
        return wallet
    }
}