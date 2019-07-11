package MinterApi

import Minter.Api
import Minter.Coin
import Minter.Minter
import Minter.MinterMatch
import khttp.get
import org.json.JSONArray
import org.json.JSONObject

class MinterApi(var nodeUrl: String? = null) {

    private val parseBlock = ParseBlock()
    private val parseNode = ParseNode()
    //    private val parseWallet = ParseWallet()
//    private val parseTransaction = ParseTransaction()
    private val parseCoin = ParseCoin()
    private val parseStatus = ParseStatus()
    private val parseEstimateCoinBuy = ParseEstimateCoinBuy()
    private val parseEstimateCoinSell = ParseEstimateCoinSell()

    private val minterMatch = MinterMatch()


    init {
        if (nodeUrl == null) nodeUrl = Api.getUrl()
    }

    private enum class Method(val patch: String) {
        BLOCK("block"),
        NODE("candidate"),
        ADDRESS("address"),
        COIN("coin_info"),
        STATUS("status"),
        ESTIMATE_COIN_BUY("estimate_coin_buy"),
        ESTIMATE_COIN_SELL("estimate_coin_sell"),
    }

    //    Transaction
    fun getBlock(
        height: Long,
        proposer_call: ((pub_key: String) -> Int?)? = null,
        transactions: ((transactions: JSONArray) -> Unit)? = null/*,
                 getOther: ((address: String) -> Int)? = null*/
    ): Minter.Block? {

        val jsonObj = this.get(Method.BLOCK, mapOf("height" to height.toString()))
//                println("getBlock($height)\n "+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseBlock.get(result, {
                proposer_call?.invoke(it)
            }, {
                if (transactions != null) {
                    transactions(it)
                }
            }) //proposer_call
        }
//        println("Error getBlock($height)")
        return null
    }

    fun getNode(
        pub_key: String,
        height: Long = 0,
        reward_address: ((address: String) -> Long)? = null,
        owner_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
//        pub_key=_&height
        val jsonObj = this.get(Method.NODE, mapOf("pub_key" to pub_key, "height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseNode.get(result, reward_address, owner_address)
        }
//        println("Error getBlock($height)")
        return null
    }

    fun getCoin(symbol: String, height: Long = 0): Minter.Coin? {
//        pub_key=_&height  symbol
        val jsonObj = this.get(Method.COIN, mapOf("symbol" to symbol, "height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseCoin.get(result)
        }
//        println("Error getBlock($height)")
        return null
    }

    fun getAddress(address: String, height: Long = 0): Minter.Wallet? {
//        pub_key=_&height
        val jsonObj = this.get(Method.ADDRESS, mapOf("address" to address, "height" to height.toString()))
//                println("getAddress($address, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }

            if (result != null) return Minter.Wallet(null, address)
//                return parseWallet.get(result)
        }
//        println("Error getBlock($height)")
        return null
    }

    fun getStatus(): Minter.Status? {
        var result: JSONObject? = null
        val jsonObj = this.get(Method.STATUS, mapOf())
        if (jsonObj != null) {
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseStatus.get(result)
//                return parseWallet.get(result)

        }
        return null
    }

    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: Double,
        coinToBuy: String,
        height: Long = 0
    ): Coin.EstimateCoinBuy? {
        return this.estimateCoinBuy(coinToSell, minterMatch.getPip(valueToBuy), coinToBuy, height)
    }

    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((symbol: String) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
//        pub_key=_&height
        val jsonObj = this.get(Method.ESTIMATE_COIN_BUY,
            mapOf(
                "coin_to_sell" to coinToSell, "value_to_buy" to valueToBuy,
                "coin_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) notFoundCoin?.invoke(coinToSell)
            })
//                println(valueToBuy)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }

            if (result != null) return parseEstimateCoinBuy.get(result)
//                return parseWallet.get(result)
        }
//        println("Error getBlock($height)")
        return null
    }

    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: Double,
        coinToBuy: String,
        height: Long = 0
    ): Coin.EstimateCoinSell? {
        return this.estimateCoinSell(coinToSell, minterMatch.getPip(valueToSell), coinToBuy, height)
    }

    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((symbol: String) -> Unit)? = null
    ): Coin.EstimateCoinSell? {
        val jsonObj = this.get(Method.ESTIMATE_COIN_SELL,
            mapOf(
                "coin_to_sell" to coinToSell, "value_to_sell" to valueToSell,
                "coin_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) {
//                    println("notFoundCoin() true")
                    notFoundCoin?.invoke(coinToSell)
                } else {
//                    println("notFoundCoin() false")
                }
            }
        )
//                println("estimateCoinSell($coinToSell, $valueToSell, $coinToBuy, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseEstimateCoinSell.get(result)
        }
        return null
    }

    private fun notFoundCoin(result: JSONObject): Boolean {
        val error = result.getJSONObject("error")
        if (error.getString("message") == "Coin to sell not exists" && error.getInt("code") == 404) {
//            println("this.notFoundCoin() true")
            return true
//            notFoundCoin?.invoke(coinToSell)
        }
//        println("this.notFoundCoin() false")
        return false
    }

    /*    /estimate_coin_buy?coin_to_sell=_&coin_to_buy=_&value_to_buy=_&height=_
     /estimate_coin_sell?coin_to_sell=_&coin_to_buy=_&value_to_sell=_&height=_*/

    private fun get(
        method: Method,
        params: Map<String, String>,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
//        println("MinterApi.get($method, $params)\n")
        val r = get(this.nodeUrl + "/" + method.patch, params = params)
        if (r.statusCode == 200) {
            return r.jsonObject
        } else if (r.statusCode == 404) {
            notFound?.invoke(r.jsonObject)
        }
//        println("Error:" +this.nodeUrl + "/" + method.patch+", params $params respond $r r.statusCode ${r.statusCode} \n${r.jsonObject}")
//        println(r)
        return null
    }
}