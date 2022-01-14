package counters.minter.sdk.minter_api


import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter.Status
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.MinterRaw.BlockRaw
import counters.minter.sdk.minter.MinterRaw.EventRaw
import counters.minter.sdk.minter.enum.BlockField
import counters.minter.sdk.minter.enum.Subscribe
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.http.FuelCoroutinesHttpApi
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.http.WebSocketOkHttp
import counters.minter.sdk.minter_api.parse.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import okhttp3.WebSocket
import org.json.JSONException
import org.json.JSONObject

class MinterCoroutinesHttpApi(httpOptions: HttpOptions) :
//OkHttpApi(httpOptions),
//FuelHttpApi(httpOptions),
    FuelCoroutinesHttpApi(httpOptions),
//    KHttpApi(httpOptions),
    AltUrlHttpGetInterface,
    CollectionConvert {

    private val parseBlock = ParseBlock()
    private val parseNode = ParseNode()
    private val parseWallet = ParseWallet()
    private val parseCoin = ParseCoin()
    private val parseStatus = ParseStatus()
    private val parseEstimateCoinBuy = ParseEstimateCoinBuy()
    private val parseEstimateCoinSell = ParseEstimateCoinSell()
    private val parseEvents = ParseEvent()
    private val parseTransaction = ParseTransaction()
    private val parseSwapPool = ParseSwapPool()
    private val parseSubscribe = ParseSubscribe()


    private val parseLimitOrder = ParseLimitOrder()

    private val minterMatch = MinterMatch()

    private val logger = KotlinLogging.logger {}

    private val webSocketOkHttp = WebSocketOkHttp(httpOptions)

    suspend fun getStatusJson(timeout: Long? = null): JSONObject? {
        return getJSONObject(this.get(HttpMethod.STATUS.patch, null, timeout))
    }

    suspend fun getStatus(timeout: Long? = null): Status? {
        getStatusJson(timeout)?.let {
            return parseStatus.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getBlockJson(height: Long, fields: HashSet<String>? = null, failed_txs: Boolean? = null, events: Boolean? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        fields?.let {
            it.forEach { params.add("fields" to it) }
//            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
        failed_txs?.let { params.add("failed_txs" to "true") }
        events?.let { params.add("events" to "true") }
        return getJSONObject(this.get(HttpMethod.BLOCK.patch + "/" + height, params, timeout))
    }

    suspend fun getBlockJson(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, events: Boolean? = null, timeout: Long? = null) =
        getBlockJson(height, conv(fields), failed_txs, events, timeout)

    suspend fun getBlockJson(height: Long, timeout: Long? = null) =
        getBlockJson(height, null as HashSet<String>?, null, null, timeout)

    suspend fun getBlock(height: Long, fields: HashSet<String>? = null, failed_txs: Boolean? = null, events: Boolean? = null, timeout: Long? = null): BlockRaw? {
        getBlockJson(height, fields, failed_txs, events, timeout)?.let {
            if (it.isNull("error")) {
                return parseBlock.getRaw(it)
            } else {
                return null
            }
        } ?: run {
            return null
        }
    }

    suspend fun getBlock(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, events: Boolean? = null, timeout: Long? = null) =
        getBlock(height, conv(fields), failed_txs, events, timeout)

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

    private fun getJSONObject(strJson: String?): JSONObject? {
        if (strJson == null) return null
        return try {
            JSONObject(strJson)
        } catch (e: JSONException) {
            logger.error { "JSONException $e" }
            null
        }
    }

    suspend fun getTransactionJson(hash: String, timeout: Long? = null): JSONObject? {
        this.get(HttpMethod.TRANSACTION.patch + "/" + hash, null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getTransaction(hash: String, timeout: Long? = null): TransactionRaw? {
        getTransactionJson(hash, timeout).let {
//            logger.info { "getBlockJson($height, $timeout) : $it" }
            if (it != null) {
//                logger.info { "getBlockJson($height, $timeout) : ${parseBlock.getRaw(it)}" }
                return parseTransaction.getRaw(it)
            } else {
                return null
            }
        }
    }


    suspend fun getLimitOrdersJson(ids: List<Long>? = null, height: Long? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        ids?.forEach {
            params.add("ids" to it.toString())
        }
        this.get(HttpMethod.LIMIT_ORDERS.patch + altUrlHttpGet(params), null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getLimitOrders(ids: List<Long>? = null, height: Long? = null, timeout: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersJson(ids, height, timeout).let {
            if (it != null) {
                return parseLimitOrder.array(it)
            } else {
                return null
            }
        }
    }

    suspend fun getLimitOrderJson(orderId: Long, height: Long? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        this.get(HttpMethod.LIMIT_ORDER.patch + "/" + orderId, params, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getLimitOrder(orderId: Long, height: Long? = null, timeout: Long? = null): LimitOrderRaw? {
        getLimitOrderJson(orderId, height, timeout).let {
            if (it != null) {
                return parseLimitOrder.get(it)
            } else {
                return null
            }
        }
    }

    suspend fun getLimitOrdersOfPoolJson(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, timeout: Long?): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        limit?.let { params.add("limit" to limit.toString()) }

        this.get(HttpMethod.LIMIT_ORDERS.patch + "/" + sellCoin + "/" + buyCoin, params, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, timeout: Long?): List<LimitOrderRaw>? {
        getLimitOrdersOfPoolJson(sellCoin, buyCoin, limit, height, timeout).let {
            if (it != null) {
                return parseLimitOrder.array(it)
            } else {
                return null
            }
        }
    }

    suspend fun getEventsJson(height: Long, search: List<String>? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        search?.forEach { params.add("search" to it) }
        this.get(HttpMethod.EVENTS.patch + "/" + height + altUrlHttpGet(params), null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getEvents(height: Long, search: List<String>? = null, timeout: Long? = null): List<EventRaw>? {
        getEventsJson(height, search, timeout).let {
            if (it != null) {
                return parseEvents.getRaw(it, height)
            } else {
                return null
            }
        }
    }

    suspend fun getAddressJson(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to height.toString()) }
        delegated?.let { if (it) params.add("delegated" to "true") }
        this.get(HttpMethod.ADDRESS.patch + "/" + address, params, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getAddress(address: String, height: Long? = null, delegated: Boolean? = null, timeout: Long? = null): AddressRaw? {
        getAddressJson(address, height, delegated, timeout).let {
            if (it != null) {
                return parseWallet.getRaw(it, address)
            } else {
                return null
            }
        }
    }

    suspend fun estimateCoinSellJson(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): JSONObject? {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_sell" to minterMatch.getPip(valueToSell),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        coin_id_commission?.let { params.add("coin_id_commission" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.get(HttpMethod.ESTIMATE_COIN_SELL.patch + altUrlHttpGet(params), null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        estimateCoinSellJson(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, timeout).let {
            if (it != null) {
                return parseEstimateCoinSell.get(it)
            } else {
                return null
            }
        }
    }

    suspend fun estimateCoinSellAllJson(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): JSONObject? {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_sell" to minterMatch.getPip(valueToSell),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        gas_price?.let { params.add("gas_price" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.get(HttpMethod.ESTIMATE_COIN_SELL_ALL.patch + altUrlHttpGet(params), null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun estimateCoinSellAll(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null
    ): Coin.EstimateCoin? {
        estimateCoinSellAllJson(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, timeout).let {
            if (it != null) {
                return parseEstimateCoinSell.get(it)
            } else {
                return null
            }
        }
    }

    suspend fun estimateCoinBuyJson(
        coinToBuy: Long,
        valueToBuy: Double,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        timeout: Long? = null,
    ): JSONObject? {
        val params = arrayListOf<Pair<String, String>>(
            "coin_id_to_sell" to coinToSell.toString(),
            "value_to_buy" to minterMatch.getPip(valueToBuy),
            "coin_id_to_buy" to coinToBuy.toString(),
        )
        coin_id_commission?.let { params.add("coin_id_commission" to it.toString()) }
        swap_from?.let { params.add("swap_from" to it.value) }
        route?.forEach { params.add("route" to it.toString()) }
        height?.let { params.add("height" to it.toString()) }
        this.get(HttpMethod.ESTIMATE_COIN_BUY.patch + altUrlHttpGet(params), null, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun estimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: Double,
        coinToSell: Long,
        height: Long?,
        coin_id_commission: Long?,
        swapFrom: SwapFromTypes?,
        route: List<Long>?,
        timeout: Long?
    ): Coin.EstimateCoin? {
        estimateCoinBuyJson(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swapFrom, route, timeout).let {
            if (it != null) {
                return parseEstimateCoinBuy.get(it)
            } else {
                return null
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun streamSubscribeJson(query: String, timeout: Long? = null): Flow<JSONObject?> = callbackFlow {
        val params = arrayListOf("query" to query)
//        webSocketOkHttp.socket(HttpMethod.SUBSCRIBE.patch, params, null, messagesListener)
        var socket: WebSocket? = null
        socket = webSocketOkHttp.socket(HttpMethod.SUBSCRIBE.patch, params, timeout) {
            getJSONObject(it)?.let {
                if (!it.isNull("result")) {
                    trySend(it).isSuccess
                } else {
                    trySend(null).isClosed
                    close()
                    webSocketOkHttp.close(socket)
                }
            } ?: run {
                logger.error { "Error: socket return" }
                trySend(null).isClosed
                close()
                webSocketOkHttp.close(socket)
            }
        }
//        webSocketOkHttp.close(socket)
//        awaitClose {  }
        awaitClose { cancel() }
    }

    private fun streamSubscribeJson(query: Subscribe, timeout: Long? = null) = streamSubscribeJson(query.str, timeout)

    fun streamSubscribeStatus(timeout: Long? = null): Flow<Status?> = flow {
        streamSubscribeJson(Subscribe.TmEventNewBlock, timeout).collect {
            if (it != null) emit(parseSubscribe.status(it)) else emit(null)
        }
    }

    suspend fun getSwapPoolJson(coin0: Long, coin1: Long, height: Long? = null, timeout: Long? = null): JSONObject? {
        val params = arrayListOf<Pair<String, String>>()
        height?.let { params.add("height" to it.toString()) }
        this.get(HttpMethod.SWAP_POOL.patch + "/" + coin0 + "/" + coin1, params, timeout).let {
            getJSONObject(it)?.let {
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    suspend fun getSwapPool(coin0: Long, coin1: Long, height: Long? = null, timeout: Long? = null): MinterRaw.SwapPoolRaw? {
        getSwapPoolJson(coin0, coin1, height, timeout).let {
            if (it != null) {
                return parseSwapPool.get(it)
            } else {
                return null
            }
        }
    }

//    companion object {}
}