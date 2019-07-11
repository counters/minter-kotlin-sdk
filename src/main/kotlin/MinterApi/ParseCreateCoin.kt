package MinterApi

import Minter.Minter.Coin
import Minter.MinterMatch
import org.json.JSONObject

object ParseCreateCoin {
    private val minterMatch = MinterMatch()
    fun get(result: JSONObject, creater: Long?, crblock: Long?): Coin? {
//        var coin: Coin? = null
        val name = result.getString("name")
        val symbol = result.getString("symbol")
        val length = symbol.length
        val constant_reserve_ratio = result.getInt("constant_reserve_ratio")
        val initrpip = result.getString("initial_reserve")
        val initreserv = minterMatch.getAmount(initrpip)
        val initial_amount = result.getString("initial_amount")
        val initamount = minterMatch.getAmount(initial_amount)
        val enabled = true
//        val crr = 0
        val coin = Coin(
            null,
            symbol,
            length,
            name,
            creater,
            constant_reserve_ratio,
            initrpip,
            initreserv,
            initial_amount,
            initamount,
            crblock,
            enabled,
            1
        )
        return coin
    }
}