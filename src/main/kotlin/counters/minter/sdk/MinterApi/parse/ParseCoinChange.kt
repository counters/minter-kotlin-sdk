package counters.minter.sdk.MinterApi.parse

import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterMatch
import counters.minter.sdk.Minter.TransactionTypes
import org.json.JSONObject

class ParseCoinChange {

    //    val transactions: MutableMap<String, Long>
//    transactions.put(hash,123)
    private val minterMatch = MinterMatch()

    fun get(result: JSONObject, height: Long, wallet: Long, coin_to_sell: Int, coin_to_buy: Int): Minter.CoinChange? {
//        var coinChange: counter.sdk.Minter.CoinChange? = null

        val type = result.getInt("type")
        val tx_return = minterMatch.getAmount(result.getJSONObject("tags").getString("tx.return"))
        val sell: Double
        val buy: Double
        if (type == TransactionTypes.TypeSellCoin) {
            sell = minterMatch.getAmount(result.getJSONObject("data").getString("value_to_sell"))
            buy = tx_return
        } else if (type == TransactionTypes.TypeSellAllCoin) {
            sell = minterMatch.getAmount(result.getJSONObject("tags").getString("tx.sell_amount"))
            buy = tx_return
        } else {
            sell = tx_return
            buy = minterMatch.getAmount(result.getJSONObject("data").getString("value_to_buy"))
        }
        val coinChange = Minter.CoinChange(height, null, type, null, wallet, sell, coin_to_sell, buy, coin_to_buy)
        return coinChange
    }
}