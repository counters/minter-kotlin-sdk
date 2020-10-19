package MinterApi

import Minter.Minter.Coin
import Minter.MinterMatch
import Minter.MinterRaw.CoinRaw
import org.json.JSONObject

class ParseCoin {

    private val minterMatch = MinterMatch()
    fun get(result: JSONObject/*, creater: ((address: String) -> Unit)? = null*/): Coin? {
//        {"volume":"225023620988028216904195498","symbol":"BTCSECURE","crr":"70","name":"BTC.Secure Coin","reserve_balance":"7182683977280929958465178"}
//        var coin: Minter.Coin? = null
        val name = result.getString("name")
        val symbol = result.getString("symbol")
        val length = symbol.length
        val crr = result.getInt("crr")

//        creater?.invoke("Mx0f9u8u8i")

        val max_supply = minterMatch.getAmount(result.getString("max_supply") )// TODO New parametr
        val volume = minterMatch.getAmount(result.getString("volume")) // TODO New parametr
        val reserve_balance = minterMatch.getAmount(result.getString("reserve_balance")) // TODO New parametr


        val coin = Coin(null, symbol, length, name, null, crr, null, null, null, null, null, true, 1)
        return coin
    }
    fun getRaw(result: JSONObject): CoinRaw? {
        val name = result.getString("name")
        val symbol = result.getString("symbol")
        val length = symbol.length
        val crr = result.getInt("crr")

        val max_supply = minterMatch.getAmount(result.getString("max_supply") )
        val volume = minterMatch.getAmount(result.getString("volume"))
        val reserve_balance = minterMatch.getAmount(result.getString("reserve_balance"))


        val coin = CoinRaw(symbol, length, name, crr, volume, reserve_balance, max_supply)
        return coin
    }
}

