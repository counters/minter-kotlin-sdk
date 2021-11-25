package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.Minter.*
import counters.minter.sdk.minter.MinterRaw.*
import counters.minter.sdk.minter_api.http.AsyncHttpApi
import counters.minter.sdk.minter_api.http.HttpOptions
import counters.minter.sdk.minter_api.parse.*
import mu.KotlinLogging
import org.json.JSONException
import org.json.JSONObject

class MinterAsyncHttpApi(httpOptions: HttpOptions): AsyncHttpApi(httpOptions) {

//    private var headers: Map<String, String>?
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
    private val parseSwapPoolRaw = ParseSwapPoolRaw()

    private val logger = KotlinLogging.logger {}

    fun getStatusJson(timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.httpGet(HttpMethod.STATUS.patch, null, timeout){
            it?.let { result( getJSONObject(it)) }
        }
    }
    fun getStatus(timeout: Long? = null, result: ((result: Status?) -> Unit)) {
        getStatusJson(timeout) {
            it?.let {
                if (!it.isNull("latest_block_height") && it.isNull("error")) {
                    result (parseStatus.get(it))
                }
            }
        }
    }

    fun getBlockJson(height: Long, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.httpGet(HttpMethod.BLOCK.patch+"/"+height, null, timeout) {
//            logger.info { "this.httpGet(*): ${it}" }
            getJSONObject(it)?.let {
//                logger.info { "this.httpGet(*): JSON ${it}" }
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
//            logger.info { "getBlockJson($height, $timeout) : $it" }
            if (it != null) {
//                logger.info { "getBlockJson($height, $timeout) : ${parseBlock.getRaw(it)}" }
                result(parseBlock.getRaw(it))
            } else { result(null) }
        }
    }

    fun getJSONObject(strJson: String?): JSONObject? {
        if (strJson==null) return null
        return try {
            JSONObject(strJson)
        } catch (e: JSONException) {
            logger.error { "JSONException $e" }
            null
        }
    }

    fun getTransactionJson(hash: String, timeout: Long? = null, result: ((result: JSONObject?) -> Unit)) {
        this.httpGet(HttpMethod.TRANSACTION.patch+"/"+hash, null, timeout) {
//            logger.info { "this.httpGet(*): ${it}" }
            getJSONObject(it)?.let {
//                logger.info { "this.httpGet(*): JSON ${it}" }
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
            } else { result(null) }
        }
    }



    companion object {
    }
}