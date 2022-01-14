package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter.Status
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.MinterRaw.BlockRaw
import counters.minter.sdk.minter.MinterRaw.EventRaw
import counters.minter.sdk.minter.enum.Subscribe
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.http.OkHttpApi
import counters.minter.sdk.minter_api.http.WebSocketOkHttp
import counters.minter.sdk.minter_api.parse.*
import okhttp3.WebSocket
import org.json.JSONObject

class MinterAsyncHttpApi(httpOptions: HttpOptions) :
    OkHttpApi(httpOptions),
//    KHttpApi(httpOptions),
    AltUrlHttpGetInterface,
    StringJSON {

    private val parseBlock = ParseBlock()

    //    private val parseNode = ParseNode()
    private val parseWallet = ParseWallet()

    //    private val parseCoin = ParseCoin()
    private val parseStatus = ParseStatus()
    private val parseEstimateCoinBuy = ParseEstimateCoinBuy()
    private val parseEstimateCoinSell = ParseEstimateCoinSell()
    private val parseEvents = ParseEvent()
    private val parseTransaction = ParseTransaction()

    //    private val parseSwapPool = ParseSwapPool()
    private val parseSubscribe = ParseSubscribe()

    private val minterMatch = MinterMatch()

    private val parseLimitOrder = ParseLimitOrder()
    private val parseSwapPool = ParseSwapPool()

//    private val logger = KotlinLogging.logger {}

    private val webSocketOkHttp = WebSocketOkHttp(httpOptions)

    fun shutdown() {
//        webSocketOkHttp.close()
    }


    fun getStatusJson(timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.asyncGet(HttpMethod.STATUS.patch, null, timeout) {
            it?.let { result(getJSONObject(it)) }
        }
    }

    fun getStatus(timeout: Long? = null, result: ((result: Status?) -> Unit)) {
        getStatusJson(timeout) {
            it?.let {
                if (!it.isNull("latest_block_height") && it.isNull("error")) {
                    result(parseStatus.get(it))
                }
            }
        }
    }

    fun getBlockJson(height: Long, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.asyncGet(HttpMethod.BLOCK.patch + "/" + height, null, timeout) {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getBlock(height: Long, timeout: Long? = null, result: ((result: BlockRaw?) -> Unit)) {
        getBlockJson(height, timeout) {
            if (it != null) {
                result(parseBlock.getRaw(it))
            } else {
                result(null)
            }
        }
    }


    fun getTransactionJson(hash: String, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.asyncGet(HttpMethod.TRANSACTION.patch + "/" + hash, null, timeout) {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getTransaction(hash: String, timeout: Long? = null, result: ((result: TransactionRaw?) -> Unit)) {
        getTransactionJson(hash, timeout) {
//            logger.info { "getBlockJson($height, $timeout) : $it" }
            if (it != null) {
//                logger.info { "getBlockJson($height, $timeout) : ${parseBlock.getRaw(it)}" }
                result(parseTransaction.getRaw(it))
            } else {
                result(null)
            }
        }
    }


    fun getLimitOrdersJson(ids: List<Long>? = null, height: Long? = null, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        ids?.forEach {
            params.add("ids" to it.toString())
        }
        this.asyncGet(HttpMethod.LIMIT_ORDERS.patch + altUrlHttpGet(params), null, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getLimitOrders(ids: List<Long>? = null, height: Long? = null, timeout: Long? = null, result: ((result: List<LimitOrderRaw>?) -> Unit)) {
        getLimitOrdersJson(ids, height, timeout) {
            if (it != null) {
                result(parseLimitOrder.array(it))
            } else {
                result(null)
            }
        }
    }

    fun getLimitOrderJson(orderId: Long, height: Long?, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        this.asyncGet(HttpMethod.LIMIT_ORDER.patch + "/" + orderId, params, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getLimitOrder(orderId: Long, height: Long?, timeout: Long?, result: (result: LimitOrderRaw?) -> Unit) {
        getLimitOrderJson(orderId, height, timeout) {
            if (it != null) {
                result(parseLimitOrder.get(it))
            } else {
                result(null)
            }
        }
    }

    fun getLimitOrdersOfPoolJson(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, timeout: Long? = null, result: (result: JSONObject?) -> Unit) {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        limit?.let { params.add("limit" to limit.toString()) }

        this.asyncGet(HttpMethod.LIMIT_ORDERS.patch + "/" + sellCoin + "/" + buyCoin, params, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, timeout: Long?, result: (result: List<LimitOrderRaw>?) -> Unit) {
        getLimitOrdersOfPoolJson(sellCoin, buyCoin, limit, height, timeout) {
            if (it != null) {
                result(parseLimitOrder.array(it))
            } else {
                result(null)
            }
        }
    }

    fun getEventsJson(height: Long, search: List<String>? = null, timeout: Long? = null, result: (result: JSONObject?) -> Unit) {
        val params = arrayListOf<Pair<String, String>>()
        search?.forEach { params.add("search" to it) }
        this.asyncGet(HttpMethod.EVENTS.patch + "/" + height + altUrlHttpGet(params), null, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getEvents(height: Long, search: List<String>? = null, timeout: Long? = null, result: (result: List<EventRaw>?) -> Unit) {
        getEventsJson(height, search, timeout) {
            if (it != null) {
                result(parseEvents.getRaw(it, height))
            } else {
                result(null)
            }
        }
    }

    fun getAddressJson(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long? = null, result: (result: JSONObject?) -> Unit) {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        delegated?.let {
            if (delegated) params.add("delegated" to "true") else params.add("delegated" to "false")
        }
        this.asyncGet(HttpMethod.ADDRESS.patch + "/" + address, params, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getAddress(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long? = null, result: (result: AddressRaw?) -> Unit) {
        getAddressJson(address, height, delegated, timeout) {
            if (it != null) {
                result(parseWallet.getRaw(it, address))
            } else {
                result(null)
            }
        }
    }

    fun getEstimateCoinSellJson(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: JSONObject?) -> Unit
    ) {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_sell" to minterMatch.getPip(valueToSell),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        coin_id_commission?.let { params.add("coin_id_commission" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.asyncGet(HttpMethod.ESTIMATE_COIN_SELL.patch + altUrlHttpGet(params), null, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinSellJson(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, timeout) {
            if (it != null) {
                result(parseEstimateCoinSell.get(it))
            } else {
                result(null)
            }
        }
    }

    fun getEstimateCoinSellAllJson(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: JSONObject?) -> Unit
    ) {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_sell" to minterMatch.getPip(valueToSell),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        gas_price?.let { params.add("gas_price" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.asyncGet(HttpMethod.ESTIMATE_COIN_SELL_ALL.patch + altUrlHttpGet(params), null, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun estimateCoinSellAll(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        gasPrice: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinSellAllJson(coinToSell, valueToSell, coinToBuy, height, gasPrice, swap_from, route, timeout) {
            if (it != null) {
                result(parseEstimateCoinSell.get(it))
            } else {
                result(null)
            }
        }
    }


    fun getEstimateCoinBuyJson(
        coinToBuy: Long,
        valueToBuy: Double,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: JSONObject?) -> Unit
    ) {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_buy" to minterMatch.getPip(valueToBuy),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        coin_id_commission?.let { params.add("coin_id_commission" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.asyncGet(HttpMethod.ESTIMATE_COIN_BUY.patch + altUrlHttpGet(params), null, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun estimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: Double,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
        result: (result: Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinBuyJson(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route, timeout) {
            if (it != null) {
                result(parseEstimateCoinBuy.get(it))
            } else {
                result(null)
            }
        }
    }

    fun streamSubscribeJson(query: Subscribe, timeout: Long? = null, result: (result: JSONObject?) -> Unit): WebSocket {
        val params = arrayListOf<Pair<String, String>>("query" to query.str)
        return webSocketOkHttp.socket(HttpMethod.SUBSCRIBE.patch, params) {
            result(getJSONObject(it))
        }
    }

    fun streamSubscribe(query: Subscribe, timeout: Long? = null, result: (result: Status?) -> Unit): WebSocket {
        return streamSubscribeJson(query, timeout) {
            if (it != null) {
                result(parseSubscribe.status(it))
            } else {
                result(null)
            }
        }
    }

    fun getSwapPoolJson(coin0: Long, coin1: Long, height: Long? = null, timeout: Long? = null, result: (result: JSONObject?) -> Unit) {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to it.toString()) }
        this.asyncGet(HttpMethod.SWAP_POOL.patch + "/" + coin0 + "/" + coin1, params, timeout)
        {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    result(it)
                } else {
                    result(null)
                }
            } ?: run { result(null) }
        }
    }

    fun getSwapPool(coin0: Long, coin1: Long, height: Long? = null, timeout: Long? = null, result: (result: MinterRaw.SwapPoolRaw?) -> Unit) {
        getSwapPoolJson(coin0, coin1, height, timeout) {
            if (it != null) {
                result(parseSwapPool.get(it))
            } else {
                result(null)
            }
        }
    }

//    companion object {}
}