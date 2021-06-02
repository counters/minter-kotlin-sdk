package counter.minter_sdk.MinterApi

import counter.minter_sdk.Minter.Minter.Coin
import counter.minter_sdk.Minter.MinterMatch
import org.json.JSONObject
import kotlin.math.roundToLong

object ParseCreateCoin {
    private val minterMatch = MinterMatch()
    fun get(data: JSONObject, tags: JSONObject, getWalletId: ((address: String) -> Long?), crblock: Long?): Coin? {
//        println(data)
//        val data = result.getJSONObject("data")
//        var coin: Coin? = null
        val name = data.getString("name")
//        val coinObj = CoinObjClass.fromJson(result.getJSONObject("name"))
        val symbol = data.getString("symbol")
        val length = symbol.length
        val constant_reserve_ratio = data.getInt("constant_reserve_ratio")
        val initrpip = data.getString("initial_reserve")
        val initreserv = minterMatch.getAmount(initrpip)
        val initial_amount = data.getString("initial_amount")
        val initamount = minterMatch.getAmount(initial_amount)
        val enabled = true
//        val owner_address ="Mx"+tags.getString("tx.from")
        val creater = getWalletId("Mx"+tags.getString("tx.from"))
        val owner_address= creater

        val volume= initamount
        val reserve_balance= initreserv
        val max_supply = minterMatch.getAmount(data.getString("max_supply") ).roundToLong()


        val mintable = data.getBoolean("mintable")
        val burnable = data.getBoolean("burnable")

        val coinId = tags.getInt("tx.coin_id")
//        val crr = 0
        val coin = Coin(
            coinId,
            symbol,
            length,
            name,
            creater,
            owner_address,
            constant_reserve_ratio,
            volume, reserve_balance, max_supply,
            mintable,
            burnable,
            initrpip,
            initreserv,
            initial_amount,
            initamount,
            crblock,
            enabled
        )
        return coin
    }
}