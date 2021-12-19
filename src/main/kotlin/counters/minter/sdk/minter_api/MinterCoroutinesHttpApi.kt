package counters.minter.sdk.minter_api


import counters.minter.sdk.minter.enum.BlockField
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter.*
import counters.minter.sdk.minter.MinterRaw.*
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.http.FuelCoroutinesHttpApi
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.parse.*
import mu.KotlinLogging
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
    private val parseSwapPoolRaw = ParseSwapPoolRaw()

    private val parseLimitOrder = ParseLimitOrder()

    private val logger = KotlinLogging.logger {}

//    override var timeout: Int? =null

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
        delegated?.let { if (it) params.add("delegated" to "true")  }
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

//    companion object {}
}