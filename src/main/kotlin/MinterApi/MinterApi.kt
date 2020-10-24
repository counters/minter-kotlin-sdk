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
        COINID("coin_info_by_id"),
        STATUS("status"),
        ESTIMATE_COIN_BUY("estimate_coin_buy"),
        ESTIMATE_COIN_SELL("estimate_coin_sell"),
        EVENTS("events"),
        TRANSACTION("transaction"),
    }

    //    get Events
    fun getEvents(height: Long, search: List<String>?=null,
                  getCoin: ((symbol: String) -> Int),
                  getWallet: ((address: String) -> Long),
                  getNode: ((address: String) -> Int),
                  getOther: ((jsonObject: JSONObject) -> Unit)? = null
    ): List<Minter.Event>? {
        val jsonObj = this.get(Method.EVENTS.patch+"/"+height)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseEvents.get(result, height, /*getCoin,*/getWallet,getNode,getOther)
        }
//        println("Error getBlock($height)")
        return null
    }    //    Transaction

    fun getEventsRaw(height: Long, search: List<String>?=null, addSymbol: Boolean= false): List<MinterRaw.EventRaw>?  {

        val jsonObj = this.get(Method.EVENTS.patch+"/"+height) // mapOf("height" to height.toString())
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            val callback = if (addSymbol) fun(coinId: Long): MinterRaw.CoinRaw? {
                return getCoinByIdRaw(coinId)
            } else null
            if (result != null) return parseEvents.getRaw(result, height, callback)
        }
//        println("Error getBlock($height)")
        return null
    }    //    Transaction

    fun getTransactionRaw(
        hash: String,
        getJson: ((transactionJson: JSONObject, height: Long) -> Unit)? = null
    ): MinterRaw.TransactionRaw? {
        val jsonObj = this.get(Method.TRANSACTION.patch+"/"+hash)
        if (jsonObj != null) {
            var result: JSONObject? = null
            var height: Long = 0
            if (jsonObj.isNull("error")) {
                result = jsonObj
                height = result.getLong("height")
                getJson?.invoke(result, height)
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
//        val jsonObj = this.get(Method.BLOCK.patch, mapOf("height" to height.toString()))
        val jsonObj = this.get(Method.BLOCK.patch+"/"+height.toString())
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
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
//        println("Error getBlock($height) $jsonObj")
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
                val pub_key = obj.getString("public_key")
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
        owner_address: ((address: String) -> Long)? = null,
        control_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
        val jsonObj = this.get(Method.NODE.patch+"/"+pub_key, mapOf("height" to height.toString()))
//      println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseNode.get(result, reward_address, owner_address, control_address)
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
        var control: String = ""
        val node = getNode(pub_key, height, {
            reward = it
            0
        }, {
            owner = it
            0
        }, {
            control = it
            0
        })
        if (node != null) {
            val minterRaw = MinterRaw.NodeRaw(
                reward = reward,
                owner = owner,
                control = control,
                pub_key = pub_key,
                commission = node.commission,
                crblock = node.crblock,
                slots = node.slots,
            users = node.users,
           min_stake = node.min_stake
            )
            return minterRaw
        }

        return null
    }

    fun getCoinById(id: Long, height: Long = 0): Minter.Coin? {
        val jsonObj = this.get(Method.COINID.patch+"/"+id, mapOf("height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseCoin.get(result)
        }
        return null
    }
    fun getCoin(id: Long, height: Long = 0): Minter.Coin? {
        return this.getCoinById(id, height)
    }
    fun getCoin(symbol: String, height: Long = 0): Minter.Coin? {
        val jsonObj = this.get(Method.COIN.patch+"/"+symbol, mapOf("height" to height.toString()))
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseCoin.get(result)
        }
        return null
    }
    fun getCoinRaw(id: Long, height: Long = 0): MinterRaw.CoinRaw? {
        return this.getCoinByIdRaw(id, height)
    }
    fun getCoinByIdRaw(id: Long, height: Long = 0): MinterRaw.CoinRaw? {
        val jsonObj = this.get(Method.COINID.patch+"/"+id, mapOf("height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseCoin.getRaw(result)
        }
        return null
    }
    fun getCoinRaw(symbol: String, height: Long = 0): MinterRaw.CoinRaw? {
        val jsonObj = this.get(Method.COIN.patch+"/"+symbol, mapOf("height" to height.toString()))
//                println("getNode($pub_key, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseCoin.getRaw(result)
        }
        return null
    }
/*    fun getCoinRaw(symbol: String, height: Long = 0): Minter.Coin? {
        return getCoin(symbol, height)
    }*/

    fun getAddress(address: String, height: Long = 0, delegated: Boolean= false): Minter.Wallet? {
        val delegated_str = if (delegated) "true" else "false"
        val jsonObj = this.get(Method.ADDRESS.patch+"/"+address, mapOf("delegated" to delegated_str, "height" to height.toString()))
//                println("getAddress($address, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null)
                return parseWallet.get(result, address)
//                return Minter.Wallet(null, address)
        }
        return null
    }

    fun getStatus(): Minter.Status? {
        var result: JSONObject? = null
        val jsonObj = this.get(Method.STATUS.patch, mapOf())
        if (jsonObj != null) {
            if (!jsonObj.isNull("latest_block_height") &&jsonObj.isNull("error")) {
//                result = jsonObj.getJSONObject("result")
                result = jsonObj
            }
            if (result != null) return parseStatus.get(result)
//                return parseWallet.get(result)
        }
//        println("getStatus()\n"+jsonObj)
        return null
    }

    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: Double,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        return this.estimateCoinBuy(coinToSell, minterMatch.getPip(valueToBuy), coinToBuy, height)
    }
    fun estimateCoinBuy(
        coinToSell: Long,
        valueToBuy: Double,
        coinToBuy: Long,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        return this.estimateCoinIdBuy(coinToSell.toString(), minterMatch.getPip(valueToBuy), coinToBuy.toString(), height)
    }

    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        val jsonObj = this.get(Method.ESTIMATE_COIN_BUY.patch,
            mapOf(
                "coin_to_sell" to coinToSell, "value_to_buy" to valueToBuy,
                "coin_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) notFoundCoin?.invoke(true)
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
    fun estimateCoinIdBuy(
        coinToSell: String,
        valueToBuy: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        val jsonObj = this.get(Method.ESTIMATE_COIN_BUY.patch,
            mapOf(
                "coin_id_to_sell" to coinToSell, "value_to_buy" to valueToBuy,
                "coin_id_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) notFoundCoin?.invoke(true)
            })
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseEstimateCoinBuy.get(result)
        }
        return null
    }

    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: Double,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinSell? {
        return this.estimateCoinSell(coinToSell, minterMatch.getPip(valueToSell), coinToBuy, height, notFoundCoin)
    }
    fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinSell? {
        return this.estimateCoinIdSell(coinToSell.toString(), minterMatch.getPip(valueToSell), coinToBuy.toString(), height, notFoundCoin)
    }

    fun estimateCoinIdSell(
        coinToSell: String,
        valueToSell: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinSell? {
        val jsonObj = this.get(Method.ESTIMATE_COIN_SELL.patch,
            mapOf(
                "coin_id_to_sell" to coinToSell, "value_to_sell" to valueToSell,
                "coin_id_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) {
                    notFoundCoin?.invoke(true)
                }
            }
        )
//        println(jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseEstimateCoinSell.get(result)
        }
        return null
    }
    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinSell? {
        val jsonObj = this.get(Method.ESTIMATE_COIN_SELL.patch,
            mapOf(
                "coin_to_sell" to coinToSell, "value_to_sell" to valueToSell,
                "coin_to_buy" to coinToBuy, "height" to height.toString()
            ), {
                if (this.notFoundCoin(it)) {
                    println("notFoundCoin() true")
                    notFoundCoin?.invoke(true)
                } else {
                    println("notFoundCoin() false")
                }
            }
        )
//        println(jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseEstimateCoinSell.get(result)
        }
        return null
    }

    private fun notFoundCoin(result: JSONObject): Boolean {
        val error = result.getJSONObject("error")
        if ( (error.getString("message") == "Coin to sell not exists" || error.getString("message") == "Coin to buy not exists") && error.getInt("code") == 102) {
//            println("this.notFoundCoin() true")
            return true
        }
//        println("this.notFoundCoin() false")
        return false
    }
    private fun get(
//        method: Method,
        patch: String,
        params: Map<String, String>? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
//        val url = this.nodeUrl + "/" + method.patch+"/2"
        val url = this.nodeUrl + "/" + patch
//        val url = this.nodeUrl + "/" + method.patch+"/" +params.getValue('height')
//        println("MinterApi.get($url, $params)\n")

        val _params = if (params == null)
            mapOf()
        else
            params

        val r = if (headers != null)
            get(url, params = _params, timeout = timeout, headers = headers)
        else
            get(url, params = _params, timeout = timeout)
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