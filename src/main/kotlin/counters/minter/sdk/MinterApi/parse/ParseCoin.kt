package counters.minter.sdk.MinterApi.parse

import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterMatch
import counters.minter.sdk.Minter.MinterRaw
import org.json.JSONObject
import kotlin.math.roundToLong

class ParseCoin {

    private val minterMatch = MinterMatch()
    fun get(result: JSONObject/*, creater: ((address: String) -> Unit)? = null*/): Minter.Coin? {
//        println(result)
//        {"volume":"225023620988028216904195498","symbol":"BTCSECURE","crr":"70","name":"BTC.Secure Coin","reserve_balance":"7182683977280929958465178"}
//        var coin: counter.sdk.Minter.Coin? = null
        val name = result.getString("name")
        val symbol = result.getString("symbol")
        val length = symbol.length
        val crr = result.getInt("crr")
        val id = result.getInt("id")
//        val owner_address = if (result.isNull("owner_address")) null else result.getString("owner_address")
        val owner_address =null
//        creater?.invoke("Mx0f9u8u8i")

        val max_supply = minterMatch.getAmount(result.getString("max_supply") ).roundToLong()
        val volume = minterMatch.getAmount(result.getString("volume"))
        val reserve_balance = minterMatch.getAmount(result.getString("reserve_balance"))
        val mintable = result.getBoolean("mintable")
        val burnable = result.getBoolean("burnable")

        val coin = Minter.Coin(
            id,
            symbol,
            length,
            name,
            null,
            owner_address,
            crr,
            volume,
            reserve_balance,
            max_supply,
            mintable,
            burnable,
            null,
            null,
            null,
            null,
            null,
            true
        )
        return coin
    }

    fun getRaw(result: JSONObject): MinterRaw.CoinRaw? {
        var coin: MinterRaw.CoinRaw? = null
        val owner_address = if (result.isNull("owner_address")) null else result.getString("owner_address")
        get(result)?.let{ coin = MinterRaw.CoinRaw(
            it.id.toLong(),
            it.symbol,
            it.name,
            owner_address,
            it.crr,
            it.volume,
            it.reserve,
            it.max_supply,
            it.mintable,
            it.burnable
        )
        }
       return coin
    }
}