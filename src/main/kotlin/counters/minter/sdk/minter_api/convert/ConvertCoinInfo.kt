package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.CoinInfoResponse
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import kotlin.math.roundToLong

class ConvertCoinInfo : MinterMatch() {
    var exception: Boolean = true

    fun get(response: CoinInfoResponse): MinterRaw.CoinRaw {
        val id = response.id
        val symbol = response.symbol
        val name = response.name
        val owner = if (response.hasOwnerAddress()) response.ownerAddress.value else null
        val crr = response.crr.toInt()
        val volume = getAmount(response.volume)
        val reserve = getAmount(response.reserveBalance)
        val max_supply = getAmount(response.maxSupply).roundToLong()
        val mintable = response.mintable
        val burnable = response.burnable

        return MinterRaw.CoinRaw(
            id = id,
            symbol = symbol,
            name = name,
            owner = owner,
            crr = crr,
            volume = volume,
            reserve = reserve,
            max_supply = max_supply,
            mintable = mintable,
            burnable = burnable,
        )
    }
}
