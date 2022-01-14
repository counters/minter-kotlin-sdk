package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.enum.QueryTags
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.http.FuelHttpApi
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.parse.*
import org.json.JSONArray
import org.json.JSONObject

class MinterHttpApiOld(
    var nodeUrl: String = "http://localhost:8843/v2",
    timeout: Double? = null,
    override var headers: Map<String, String>? = null
) :
    FuelHttpApi(
        HttpOptions(
            raw = nodeUrl,
            timeout = (timeout?.times(1000.0))?.toLong(),
            headers = headers
        )
    ),
    AltUrlHttpGetInterface, CollectionConvert, StringJSON {

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
    private val parseSwapPool = ParseSwapPool()
    private val parseLimitOrder = ParseLimitOrder()

    private val minterMatch = MinterMatch()

//    private val timeout = timeout ?: khttp.DEFAULT_TIMEOUT

    //    get Events
    fun getEvents(
        height: Long, search: List<String>? = null,
        getCoin: ((symbol: String) -> Int),
        getWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
        getOther: ((jsonObject: JSONObject) -> Unit)? = null
    ): List<Minter.Event>? {
        val jsonObj = this.getJson(HttpMethod.EVENTS.patch + "/" + height)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }
            if (result != null) return parseEvents.get(result, height, /*getCoin,*/getWallet, getNode, getOther)
        }
//        println("Error getBlock($height)")
        return null
    }    //    Transaction

    fun getEventsRaw(height: Long, search: List<String>? = null, addSymbol: Boolean = false): List<MinterRaw.EventRaw>? {
        val params = arrayListOf<Pair<String, String>>()
        search?.forEach { params.add("search" to it) }
        val jsonObj = this.getJson(HttpMethod.EVENTS.patch + "/" + height + altUrlHttpGet(params), mapOf()) // mapOf("height" to height.toString())
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

    fun getTransactionsRaw(
        query: Map<QueryTags, String>,
        page: Int = 1,
        per_page: Int? = null,
        getJson: ((transactionJson: JSONObject) -> Unit)? = null
    ): List<TransactionRaw>? {
        val newQuery = mutableMapOf<String, String>()
        query.forEach { newQuery[it.key.str] = it.value }
        return _getTransactionsRaw(newQuery, page, per_page, getJson)
    }

    private fun _getTransactionsRaw(
        query: Map<String, String>,
        page: Int? = null,
        per_page: Int? = null,
        getJson: ((transactionJson: JSONObject) -> Unit)? = null
    ): List<TransactionRaw>? {
        per_page?.let { if (it > Conf.maxPerPage) return null }
        if (query.count() > 0) {
            val params = arrayListOf<String>()
            query.forEach {
                val value = if (it.value.length == 42) it.value.drop(2) else it.value
                params.add("${it.key}=%27$value%27")
            }
            var strQuery = "query=" + params.joinToString("%20AND%20")
            if (page != null) strQuery += "&page=$page"
            if (per_page != null) strQuery += "&per_page=$per_page"
            val patch = "transactions?$strQuery"
//            println("patch $patch")
            this.getJson(patch)?.optJSONArray("transactions")?.let {
                val arrayList = arrayListOf<TransactionRaw>()
                it.forEach { transactionJson ->
                    parseTransaction.getRaw(transactionJson as JSONObject, 0)?.let {
                        arrayList.add(it)
                    }
                    getJson?.invoke(transactionJson)
                }
                return arrayList
            }/*?: run {
                    return null
                }*/
            /*    val array = it.get
                var height: Long = 0
                if (it.isNull("error")) {
                    height = it.getLong("height")
                    getJson?.invoke(it, height)
                }
                return parseTransaction.getRaw(it, height)*/
//            }
        }
        return null
    }

    fun getTransactionRaw(
        hash: String,
        getJson: ((transactionJson: JSONObject, height: Long) -> Unit)? = null
    ): TransactionRaw? {
        val jsonObj = this.getJson(HttpMethod.TRANSACTION.patch + "/" + hash)
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
//        val jsonObj =  this.getJson(Method.BLOCK.patch, mapOf("height" to height.toString()))
        val jsonObj = this.getJson(HttpMethod.BLOCK.patch + "/" + height.toString())
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
        val jsonObj = this.getJson(HttpMethod.BLOCK.patch + "/" + height.toString())
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) {
                return parseBlock.getRaw(result)
            }
        }
        return null
    }


/*    fun getBlockRawOld(height: Long): MinterRaw.BlockRaw? {
        var proposer: String = ""
        val transaction = ArrayList<TransactionRaw>()
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
//                block.total_txs,
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
    }*/

    fun getNode(
        pub_key: String,
        height: Long = 0,
        reward_address: ((address: String) -> Long)? = null,
        owner_address: ((address: String) -> Long)? = null,
        control_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
        val jsonObj = this.getJson(HttpMethod.NODE.patch + "/" + pub_key, mapOf("height" to height.toString()))
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
        val jsonObj = this.getJson(HttpMethod.COINID.patch + "/" + id, mapOf("height" to height.toString()))
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
        val jsonObj = this.getJson(HttpMethod.COIN.patch + "/" + symbol, mapOf("height" to height.toString()))
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
        val jsonObj = this.getJson(HttpMethod.COINID.patch + "/" + id, mapOf("height" to height.toString()))
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
        val jsonObj = this.getJson(HttpMethod.COIN.patch + "/" + symbol, mapOf("height" to height.toString()))
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

    /*    fun getCoinRaw(symbol: String, height: Long = 0): counter.sdk.Minter.Coin? {
            return getCoin(symbol, height)
        }*/

    fun getAddressJson(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long?=null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        delegated?.let {
            if (delegated) params.add("delegated" to "true") else params.add("delegated" to "false")
        }
        return getJSONObject(get(HttpMethod.ADDRESS.patch + "/" + address, params, timeout))
    }

    fun getAddressRaw(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long?=null): AddressRaw? {
        getAddressJson(address, height, delegated, timeout)?.let {
            return parseWallet.getRaw(it, address)
        }
        return null
    }

    fun getAddress(address: String, height: Long = 0, delegated: Boolean = false): Minter.Wallet? {
        val delegated_str = if (delegated) "true" else "false"
        val jsonObj = this.getJson(HttpMethod.ADDRESS.patch + "/" + address, mapOf("delegated" to delegated_str, "height" to height.toString()))
//                println("getAddress($address, $height)\n"+jsonObj)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null)
                return parseWallet.get(result, address)
//                return counter.sdk.Minter.Wallet(null, address)
        }
        return null
    }

    fun getStatus(): Minter.Status? {
        var result: JSONObject? = null
        val jsonObj = this.getJson(HttpMethod.STATUS.patch, mapOf())
        if (jsonObj != null) {
            if (!jsonObj.isNull("latest_block_height") && jsonObj.isNull("error")) {
//                result = jsonObj.getJSONObject("result")
                result = jsonObj
            }

            if (result != null) return parseStatus.get(result)
//                return parseWallet.get(result)
        }
//        println("getStatus()\n"+jsonObj)
        return null
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: Double,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        return this.estimateCoinBuy(coinToSell, minterMatch.getPip(valueToBuy), coinToBuy, height)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun estimateCoinBuy(
        coinToSell: Long,
        valueToBuy: Double,
        coinToBuy: Long,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        return this.estimateCoinIdBuyOld(coinToSell.toString(), minterMatch.getPip(valueToBuy), coinToBuy.toString(), height)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "not support for tokens")
    fun estimateCoinBuy(
        coinToSell: String,
        valueToBuy: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        val jsonObj = this.getJson(HttpMethod.ESTIMATE_COIN_BUY.patch,
            mapOf(
                "coin_to_sell" to coinToSell, "value_to_buy" to valueToBuy,
                "coin_to_buy" to coinToBuy, "height" to height.toString()
            ), null, {
                if (this.notFoundCoin(it)) notFoundCoin?.invoke(true)
            })
//                println(valueToBuy)
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (!jsonObj.isNull("result")) {
                result = jsonObj.getJSONObject("result")
            }

            if (result != null) return parseEstimateCoinBuy.getOld(result)
        }
//        println("Error getBlock($height)")
        return null
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun estimateCoinIdBuyOld(
        coinToSell: String,
        valueToBuy: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoinBuy? {
        val jsonObj = this.getJson(HttpMethod.ESTIMATE_COIN_BUY.patch,
            mapOf(
                "coin_id_to_sell" to coinToSell, "value_to_buy" to valueToBuy,
                "coin_id_to_buy" to coinToBuy, "height" to height.toString()
            ), null, {
                if (this.notFoundCoin(it)) notFoundCoin?.invoke(true)
            })
        if (jsonObj != null) {
            var result: JSONObject? = null
            if (jsonObj.isNull("error")) {
                result = jsonObj
            }
            if (result != null) return parseEstimateCoinBuy.getOld(result)
        }
        return null
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "not full support")
    fun estimateCoinIdBuy(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        val jsonObj = this.getJson(HttpMethod.ESTIMATE_COIN_BUY.patch,
            mapOf(
                "coin_id_to_sell" to coinToSell.toString(), "value_to_buy" to valueToBuy,
                "coin_id_to_buy" to coinToBuy.toString(), "height" to height.toString()
            ), null, {
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

/*    @Deprecated(level = DeprecationLevel.WARNING, message = "not full support")
    fun estimateCoinIdBuy(
        coinToBuy: String,
        valueToBuy: String,
        coinToSell: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    )= estimateCoinIdBuy(coinToBuy, minterMatch.getPip(valueToSell),coinToSell,  height, notFoundCoin)
    */

    @Deprecated(level = DeprecationLevel.WARNING, message = "not support for tokens")
    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: Double,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        return this.estimateCoinSell(coinToSell, minterMatch.getPip(valueToSell), coinToBuy, height, notFoundCoin)
    }

    fun estimateCoinIdSellAll(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
//        val swap_from_str =  SwapFromTypes.values().filter { it=swa }
        return this.estimateCoinIdSellAll(
            coinToSell.toString(), minterMatch.getPip(valueToSell), coinToBuy.toString(), height, coin_id_commission,
            swap_from?.value, route, notFoundCoin
        )
    }

    fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
//        val swap_from_str =  SwapFromTypes.values().filter { it=swa }
        return this.estimateCoinIdSell(
            coinToSell.toString(), minterMatch.getPip(valueToSell), coinToBuy.toString(), height, coin_id_commission,
            swap_from?.value, route, notFoundCoin
        )
    }

    fun estimateCoinIdSell(
        coinToSell: String?,
        valueToSell: String,
        coinToBuy: String,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: String? = null,
        route: List<Long>? = null,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        val params = mutableMapOf<String, String>("value_to_sell" to valueToSell)
        if (coinToSell != null) params["coin_id_to_sell"] = coinToSell
        if (coinToBuy != null) params["coin_id_to_buy"] = coinToBuy
        if (height != null) params["height"] = height.toString()

        if (coin_id_commission != null) params["coin_id_commission"] = coin_id_commission.toString()
        if (swap_from != null) params["swap_from"] = swap_from
        var addPathForURL = ""

        if (route != null) {
//            val newRoute= listOf<Long>(2024,1994,1678,1087,0,2024)
            val array = arrayListOf<String>()
            route.forEach { array.add("route=$it") }


            params.forEach { k, v ->
                array.add("$k=$v")
            }

            addPathForURL = "?" + array.joinToString("&")
            params.clear()
        }

        val jsonObj = this.getJson(
            HttpMethod.ESTIMATE_COIN_SELL.patch + addPathForURL, params, null
        ) {
            if (this.notFoundCoin(it)) {
                notFoundCoin?.invoke(true)
            }
        }
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

    fun estimateCoinIdSellAll(
        coinToSell: String?,
        valueToSell: String,
        coinToBuy: String,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: String? = null,
        route: List<Long>? = null,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        val params = mutableMapOf<String, String>("value_to_sell" to valueToSell)
        if (coinToSell != null) params["coin_id_to_sell"] = coinToSell
        if (coinToBuy != null) params["coin_id_to_buy"] = coinToBuy
        if (height != null) params["height"] = height.toString()

        if (coin_id_commission != null) params["gas_price"] = coin_id_commission.toString()
        if (swap_from != null) params["swap_from"] = swap_from
        var addPathForURL = ""

        if (route != null) {
            val array = arrayListOf<String>()
            route.forEach { array.add("route=$it") }
            params.forEach { k, v ->
                array.add("$k=$v")
            }
            addPathForURL = "?" + array.joinToString("&")
            params.clear()
        }

        this.getJson("estimate_coin_sell_all" + addPathForURL, params, {
            if (this.notFoundCoin(it)) {
                notFoundCoin?.invoke(true)
            }
        }
        )?.let {
            if (it.isNull("error")) {
                return parseEstimateCoinSell.get(it)
            }
        }
        return null
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "not support for tokens")
    fun estimateCoinSell(
        coinToSell: String,
        valueToSell: String,
        coinToBuy: String,
        height: Long = 0,
        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        val jsonObj = this.getJson(HttpMethod.ESTIMATE_COIN_SELL.patch,
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
        if ((error.getString("message") == "Coin to sell not exists" || error.getString("message") == "Coin to buy not exists") && error.getInt("code") == 102) {
//            println("this.notFoundCoin() true")
            return true
        }
//        println("this.notFoundCoin() false")
        return false
    }


/*    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    private fun get(
//        method: Method,
        patch: String,
        params: Map<String, String>? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
//        val url = this.nodeUrl + "/" + method.patch+"/2"
        val url = this.nodeUrl + "/" + patch
//        val url = this.nodeUrl + "/" + method.patch+"/" +params.getValue('height')
        println("MinterApi.get($url, $params)\n")
        val _params = if (params == null)
            mapOf()
        else
            params

        val r = if (headers != null)
            khttp.get(url, params = _params, timeout = timeout, headers = headers)
        else
            khttp.get(url, params = _params, timeout = timeout)
        if (r.statusCode == 200) {
            return r.jsonObject
        } else if (r.statusCode == 404) {
            notFound?.invoke(r.jsonObject)
        }
//        println("Error:" +this.nodeUrl + "/" + method.patch+", params $params respond $r r.statusCode ${r.statusCode} \n${r.jsonObject}")
//        println(r)
        return null
    }*/

    private fun get(patch: String) = this.get(patch, mapOf(), null)

/*    private fun get(
        patch: String,
        params: List<Pair<String, String>>? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        return this.get(patch, params?.toMap(), notFound)
    }*/

    private fun get(
        patch: String,
        params: Map<String, String>? = null,
        timeout: Long? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): String? {
        return this.get(patch = patch, params = conv(params)/*, notFound = notFound*/, timeout = timeout)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    private fun getJson(
        patch: String,
        params: List<Pair<String, String>>? = null,
        timeout: Long? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        return this.getJson(patch = patch, params = conv(params)/*, notFound = notFound*/, timeout = timeout)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    private fun getJson(
        patch: String,
        params: Map<String, String>? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        return getJSONObject(get(patch = patch, params = params, notFound = notFound))
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    private fun getJson(
        patch: String,
        params: Map<String, String>? = null,
        timeout: Long? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        return getJSONObject(get(patch = patch, params = conv(params)/*, notFound = notFound*/, timeout = timeout))
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun postToJson(
        patch: String,
        params: List<Pair<String, String>>? = null,
        tx: String? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
    ): JSONObject? {
        return getJSONObject(post(patch = patch, params = null, tx = tx, timeout = timeout))

    }


/*
    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    private fun post(
        patch: String,
        params: Map<String, String>? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        val url = this.nodeUrl + "/" + patch
        val _params = if (params == null) mapOf() else params

        val _headers =  mutableMapOf<String, String>()
        if (headers != null) _headers.putAll(headers)
        _headers.put("Content-Type", "application/json; charset=UTF-8")

        val r = if (headers != null)
            khttp.post(url, params = _params, timeout = timeout, headers = headers, json = _params)
        else
            khttp.post(url, params = _params, timeout = timeout, json = _params)
        if (r.statusCode == 200) {
            return r.jsonObject
        } else if (r.statusCode == 404) {
            notFound?.invoke(r.jsonObject)
        }
//        println("Error:" +this.nodeUrl + "/" + method.patch+", params $params respond $r r.statusCode ${r.statusCode} \n${r.jsonObject}")
//        println(r.jsonObject)
        return null
    }
*/

    fun getMinGasPrice(): Int? {
        this.getJson("min_gas_price")?.let {
            if (it.isNull("error")) {
                if (!it.isNull("min_gas_price")) return it.getInt("min_gas_price")
            }
        }
        return null
    }

    fun getMaxGasPrice(height: Long? = null): Int? {
        val params = if (height != null) mapOf("height" to height.toString()) else null
        this.getJson("max_gas_price", params)?.let {
            if (it.isNull("error")) {
                if (!it.isNull("max_gas_price")) return it.getInt("max_gas_price")
            }
        }
        return null
    }

    fun sendTransaction(tx: String): String? {
//        this.post("send_transaction", mapOf("tx" to tx) )?.let {
        this.postToJson("send_transaction", null, tx)?.let {
            if (it.isNull("error")) {
                if (!it.isNull("code")) {
                    if (it.getInt("code") == 0 && !it.isNull("hash")) {
                        return it.getString("hash")
                    }
                }
            }
        }
        return null
    }

    fun getNonce(address: String): Long? {
        getAddress(address)?.let {
            return it.count_txs.plus(1)
        }
        return null
    }

    fun getSwapPool(coin0: Long, coin1: Long, height: Long? = 0): MinterRaw.SwapPoolRaw? {
        val params = if (height != null) mapOf("height" to height.toString()) else null
        this.getJson("swap_pool/$coin0/$coin1", params)?.let {
            if (it.isNull("error")) {
                return parseSwapPool.get(it)
            }
        }
        return null
    }


    fun getSwapPool(coin0: Long, coin1: Long, address: String, height: Long? = 0): MinterRaw.SwapPoolRaw? {
        val params = if (height != null) mapOf("height" to height.toString()) else null
        this.getJson("swap_pool/$coin0/$coin1/$address", params)?.let {
            if (it.isNull("error")) {
                return parseSwapPool.get(it)
            }
        }
        return null
    }

    fun getLimitOrderJson(orderId: Long, height: Long? = null, deadline: Long? = null): JSONObject? {
        val params = if (height != null) mapOf("height" to height.toString()) else null
        this.getJson(HttpMethod.LIMIT_ORDER.patch + "/" + orderId, params)?.let {
            return it
        }
        return null
    }

    fun getLimitOrder(orderId: Long, height: Long? = null, deadline: Long? = null): LimitOrderRaw? {
        getLimitOrderJson(orderId, height, deadline)?.let {
            if (it.isNull("error")) {
                return parseLimitOrder.get(it)
            }
        }
        return null
    }

    fun getLimitOrdersJson(ids: List<Long>? = null, height: Long? = null, deadline: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        ids?.forEach {
            params.add("ids" to it.toString())
        }

        this.getJson(HttpMethod.LIMIT_ORDERS.patch + altUrlHttpGet(params), mapOf())?.let {
//            println(it)
            return it
        }
        return null
    }

    fun getLimitOrders(ids: List<Long>, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersJson(ids, height, deadline)?.let {
//            println(it)
            if (it.isNull("error")) {
                return parseLimitOrder.array(it)
            }
        }
        return null
    }

    private fun getLimitOrdersOfPoolJson(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?): JSONObject? {
        val params = arrayListOf<Pair<String, String>>(
//            "sell_coin" to sellCoin.toString(),
//            "buy_coin" to buyCoin.toString()
        )

        height?.let { params.add("height" to height.toString()) }
        limit?.let { params.add("limit" to limit.toString()) }

        this.getJson(HttpMethod.LIMIT_ORDERS.patch + "/" + sellCoin + "/" + buyCoin, params)?.let {
            return it
        }
        return null
    }

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersOfPoolJson(sellCoin, buyCoin, limit, height)?.let {
//            println(it)
            if (it.isNull("error")) {
                return parseLimitOrder.array(it)
            }
        }
        return null
    }


}