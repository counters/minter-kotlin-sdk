package MinterApi

import Minter.Minter
import org.json.JSONObject

class ParseCoin {
    fun get(result: JSONObject): Minter.Coin? {
//        var coin: Minter.Coin? = null

        val name = result.getString("name")
        val symbol = result.getString("symbol")
        val length = symbol.length
        val crr = result.getInt("crr")
        val coin = Minter.Coin(null, symbol, length, name, null, crr, null, null, null, null, null, true, 1)
        return coin
    }
}

