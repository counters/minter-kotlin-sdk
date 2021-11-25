package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.Coin
import counters.minter.grpc.client.MultiSendData
import counters.minter.grpc.client.SendData
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import org.json.JSONArray
import org.json.JSONObject

class MultiSendAdv {
    private var minterMatch = MinterMatch()

    data class MultiSendAdvData(
        val optList: ArrayList<MinterRaw.MultisendItemRaw> = arrayListOf(),
        var coin: CoinObjClass.CoinObj? = null,
        var amount: Double? = null,

        )

    fun get(data_list: JSONArray): MultiSendAdvData {
        val sendData = arrayListOf<SendData>()
        data_list.forEach {
            val innerJsonObject = it as JSONObject
            val coinInMultisendStr = innerJsonObject.getJSONObject("coin")
            val coinInMultisend = coinInMultisendStr.getString("id").toLong()
            val coinInMultisendSymbol = coinInMultisendStr.getString("symbol")
            val valueInMultisend = innerJsonObject.getString("value")

            val coinData = Coin.newBuilder().setId(coinInMultisend).setSymbol(coinInMultisendSymbol)
            val element = SendData.newBuilder()
                .setTo(innerJsonObject.getString("to"))
                .setCoin(coinData)
                .setValue(valueInMultisend)
                .build()
            sendData.add(element)
        }
        return get(sendData)
    }

    fun get(data: MultiSendData): MultiSendAdvData {
        return get(data.listList)
    }

    fun get(sendData: List<SendData>): MultiSendAdvData {
        val out = MultiSendAdvData()

        var globalAmountInMultisend: Double? = 0.0;
        var globalCoinInMultisend: Long? = null
        var globalCoinInMultisendSymbol: String? = null

        sendData.forEach {
            val coinInMultisend = it.coin.id
            val coinInMultisendSymbol = it.coin.symbol
            val currValue = minterMatch.getAmount(it.value)

            val item = MinterRaw.MultisendItemRaw(
                it.to,
                currValue,
                CoinObjClass.CoinObj(coinInMultisend, coinInMultisendSymbol)
            )
            out.optList.add(item)

            if (globalCoinInMultisend == null || globalCoinInMultisend == coinInMultisend) {
                globalCoinInMultisend = coinInMultisend
                globalCoinInMultisendSymbol = coinInMultisendSymbol
                if (globalAmountInMultisend != null) globalAmountInMultisend = globalAmountInMultisend!!.plus(currValue)
            } else {
                globalAmountInMultisend = null
                globalCoinInMultisend = null
            }
        }

        if (globalCoinInMultisend != null) {
            out.coin = CoinObjClass.CoinObj(globalCoinInMultisend!!, globalCoinInMultisendSymbol!!)
            out.amount = globalAmountInMultisend
        }
        return out
    }
}