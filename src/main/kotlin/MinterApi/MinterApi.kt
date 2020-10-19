package MinterApi

import Minter.*
import khttp.get
import org.json.JSONArray
import org.json.JSONObject

class MinterApi(
    var nodeUrl: String? = null,
    val timeout: Double = 30.0,
    val headers: Map<String, String>? = null
) {

    private val parseBlock = ParseBlock()
    private val parseNode = ParseNode()
    private val parseWallet = ParseWallet()
    //    private val parseTransaction = ParseTransaction()
    private val parseCoin = ParseCoin()
    private val parseStatus = ParseStatus()
    private val parseEstimateCoinBuy = ParseEstimateCoinBuy()
    private val parseEstimateCoinSell = ParseEstimateCoinSell()
    private val parseEvents = ParseEvent()
    private val parseTransaction = ParseTransaction()

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
        EVENTS("events"),
        TRANSACTION("transaction"),
    }

    //    get Events
    fun getEvents(height: Long,
                  getCoin: ((symbol: String) -> Int),
                  getWallet: ((address: String) -> Long),
                  getNode: ((address: String) -> Int),
                  getOther: ((jsonObject: JSONObject) -> Unit)? = null
    ): List<Minter.Event>? {

        val jsonObj = this.get(Method.EVENTS, mapOf("height" to height.toString()))
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseEvents.get(result, height, getCoin,getWallet,getNode,getOther)
        }
//        println("Error getBlock($height)")
        return null
    }    //    Transaction

    fun getEventsRaw(height: Long): List<MinterRaw.EventRaw>? {

        val jsonObj = this.get(Method.EVENTS, mapOf("height" to height.toString()))
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseEvents.getRaw(result, height)
        }
//        println("Error getBlock($height)")
        return null
    }    //    Transaction

    fun getTransactionRaw(hash: String): MinterRaw.TransactionRaw? {
        val jsonObj = this.get(Method.TRANSACTION, mapOf("hash" to hash))
        if (jsonObj != null) {
            var result: JSONObject? = null
            var height: Long = 0
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
                height = result.getLong("height");
            }
            if (result != null) return parseTransaction.getRaw(result, height)
        }
//        println("Error getBlock($height)")
        return null
    }
//    val validators = ArrayList<MinterRaw.SignedValidatorsRaw>()
    fun getBlock(
        height: Long,
        proposer_call: ((pub_key: String) -> Int?)? = null,
        transactions: ((transactions: JSONArray) -> Unit)? = null,
        validators_call: ((validators: JSONArray) -> Unit)? = null
    ): Minter.Block? {
        val jsonObj = this.get(Method.BLOCK, mapOf("height" to height.toString()))
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) {
                return parseBlock.get(result, {
                    proposer_call?.invoke(it)
                }, {
                    if (transactions != null) {
                        transactions(it)
                    }
                }, {
                            validators_call?.invoke(it)

                }) //proposer_call
            }
        }
//        println("Error getBlock($height)")
        return null
    }

    fun getBlockRaw(height: Long): MinterRaw.BlockRaw? {
        var proposer: String = ""
        val transaction = ArrayList<MinterRaw.TransactionRaw>()
//        val validators = ArrayList<MinterRaw.SignedValidatorsRaw>()
        val transaction_json = ArrayList<JSONObject>()
        val signedValidators = ArrayList<MinterRaw.SignedValidatorsRaw>()

        val parseTransaction = ParseTransaction()
        val block = getBlock(height, {
            proposer = it
            0
        }, {
            it.forEach {
                val obj = it as JSONObject
                transaction_json.add(obj)
                parseTransaction.getRaw(obj, height)?.let {
                    transaction.add(it)
                }
            }
        }, {
            it.forEach { node_sign ->
                val obj = node_sign as JSONObject
                val pub_key = obj.getString("pub_key")
                val signed = obj.getBoolean("signed")
//                       val nodeId = getNode?.invoke(pub_key)
                val signedValidator = MinterRaw.SignedValidatorsRaw(pub_key, signed)
                signedValidators.add(signedValidator)
            }
        })
        if (block != null) {
            val blockRaw = MinterRaw.BlockRaw(
                height,
                block.time,
                block.num_txs,
                block.total_txs,
                block.reward,
                block.size,
                proposer,
                transaction,
                signedValidators,
                null
//                transaction_json
            )
            return blockRaw
        }
        return null
    }

    fun getNode(
        pub_key: String,
        height: Long = 0,
        reward_address: ((address: String) -> Long)? = null,
        owner_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
        val jsonObj = this.get(Method.NODE, mapOf("pub_key" to pub_key, "height" to height.toString()))
//      println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseNode.get(result, reward_address, owner_address)
        }
//        println("Error getNode($pub_key)")
        return null
    }

    fun getNodeRaw(
        pub_key: String,
        height: Long = 0
    ): MinterRaw.NodeRaw? {
        var reward: String = ""
        var owner: String = ""
        val node = getNode(pub_key, height, {
            reward = it
            0
        }, {
            owner = it
            0
        })
        if (node != null) {
            val minterRaw = MinterRaw.NodeRaw(
                reward = reward,
                owner = owner,
                pub_key = pub_key,
                commission = node.commission,
                crblock = node.crblock
            )
            return minterRaw
        }

        return null
    }

    fun getCoin(symbol: String, height: Long = 0): Minter.Coin? {
        val jsonObj = this.get(Method.COIN, mapOf("symbol" to symbol, "height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseCoin.get(result)
        }
        return null
    }
    fun getCoinRaw(symbol: String, height: Long = 0): MinterRaw.CoinRaw? {
        val jsonObj = this.get(Method.COIN, mapOf("symbol" to symbol, "height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseCoin.getRaw(result)
        }
        return null
    }
/*    fun getCoinRaw(symbol: String, height: Long = 0): Minter.Coin? {
        return getCoin(symbol, height)
    }*/

    fun getAddress(address: String, height: Long = 0): Minter.Wallet? {
        val jsonObj = this.get(Method.ADDRESS, mapOf("address" to address, "height" to height.toString()))
//                println("getAddress($address, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null)
                return parseWallet.get(result, address)
//                return Minter.Wallet(null, address)
        }
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
    private fun get(
        method: Method,
        params: Map<String, String>,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        val url = this.nodeUrl + "/" + method.patch
//        println("MinterApi.get($method, $params)\n")
        val r = if (headers != null)
            get(url, params = params, timeout = timeout, headers = headers)
        else
            get(url, params = params, timeout = timeout)
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