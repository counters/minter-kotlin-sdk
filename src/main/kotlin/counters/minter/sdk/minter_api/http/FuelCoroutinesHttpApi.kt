package counters.minter.sdk.minter_api.http

import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import mu.KotlinLogging

open class FuelCoroutinesHttpApi(httpOptions: HttpOptions ): FuelRequest {

    private val logger = KotlinLogging.logger {}
    private val httpOptions: HttpOptions
    final override var headers: Map<String, String>? = null

    private val nodeUrl: String

    override var timeout: Int? =null

    init {
        this.httpOptions = httpOptions
        headers = httpOptions.headers
        httpOptions.timeout?.let { timeout=it.toInt() }
        nodeUrl = httpOptions.raw!!
    }

    suspend fun get(
        patch: String,
        params: List<Pair<String, String>>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
    ) : String? {
//        println("getRequest(${this.nodeUrl} + \"/\" + $patch, $params, $timeout)")
        try {
//            return getRequest(this.nodeUrl + "/" + patch, params, timeout).awaitString()
            val (request, response, result) = getRequest(this.nodeUrl + "/" + patch, params, timeout).awaitStringResponseResult()
            val statusCode= response.statusCode
            result.fold(
                { data ->
                    if (statusCode == 200)
                        return data
                    else error?.invoke(data)
                },
                { error ->
                    println("An error of type ${error.exception} happened: ${error.message}")
                }
            )
        } catch (exception: Exception) {
            when (exception){
                is HttpException -> println("A network request exception was thrown: ${exception.message}")
//                is JsonMappingException -> println("A serialization/deserialization exception was thrown: ${exception.message}")
                else -> println("An exception [${exception.javaClass.simpleName}\"] was thrown")
            }
        }
        return null
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "?")
    fun asyncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
        TODO()
    }
    @Deprecated(level = DeprecationLevel.WARNING, message = "?")
    fun syncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
//        error: ((result: String) -> Unit)? = null,
    ) : String? {
        TODO()
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "?")
    fun httpPost(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
        TODO()
    }

}