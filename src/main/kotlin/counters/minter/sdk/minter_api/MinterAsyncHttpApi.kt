package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter.*
import counters.minter.sdk.minter.MinterRaw.*
import counters.minter.sdk.minter.Models.TransactionRaw
import counters.minter.sdk.minter_api.http.OkHttpApi
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.http.KHttpApi
import counters.minter.sdk.minter_api.parse.*
import mu.KotlinLogging
import org.json.JSONException
import org.json.JSONObject

class MinterAsyncHttpApi(httpOptions: HttpOptions) :
//OkHttpApi(httpOptions)
    KHttpApi(httpOptions),
    AltUrlHttpGetInterface {

    private val parseBlock = ParseBlock()
    private val parseNode = ParseNode()
    private val parseWallet = ParseWallet()
    private val parseCoin = ParseCoin()
    private val parseStatus = ParseStatus()
    private val parseEstimateCoinBuy = ParseEstimateCoinBuy()
    private val parseEstimateCoinSell = ParseEstimateCoinSell()
    private val parseEvents = ParseEvent()
    private val parseTransaction = ParseTransaction()
    private val parseSwapPoolRaw = ParseSwapPoolRaw()

    private val parseLimitOrder = ParseLimitOrder()

    private val logger = KotlinLogging.logger {}

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

    fun test_getBlockJson(height: Long, timeout: Long? = null): JSONObject? {
        this.syncGet(HttpMethod.BLOCK.patch + "/" + height, null, timeout).let {
//            logger.info { "this.httpGet(*): ${it}" }
            getJSONObject(it)?.let {
//                logger.info { "this.httpGet(*): JSON ${it}" }
                if (it.isNull("error")) {
                    return it
                } else {
                    return null
                }
            } ?: run { return null }
        }
    }

    fun test_getBlock(height: Long, timeout: Long? = null): BlockRaw? {
        test_getBlockJson(height, timeout).let {
//            logger.info { "getBlockJson($height, $timeout) : $it" }
            if (it != null) {
//                logger.info { "getBlockJson($height, $timeout) : ${parseBlock.getRaw(it)}" }
                return parseBlock.getRaw(it)
            } else {
                return null
            }
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

    private fun getJSONObject(strJson: String?): JSONObject? {
        if (strJson == null) return null
        return try {
            JSONObject(strJson)
        } catch (e: JSONException) {
            logger.error { "JSONException $e" }
            null
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

    fun getLimitOrdersOfPoolJson(sellCoin: Long, buyCoin: Long, limit: Int?, height: Long?, timeout: Long?, result: (result: JSONObject?) -> Unit) {
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

    fun getEventsJson(height: Long, search: List<String>? = null, timeout: Long? = null, result: (result: JSONObject?) -> Unit ) {
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
        getEventsJson(height,search,timeout) {
            if (it != null) {
                result(parseEvents.getRaw(it, height))
            } else {
                result(null)
            }
        }
    }

    companion object {
    }
}