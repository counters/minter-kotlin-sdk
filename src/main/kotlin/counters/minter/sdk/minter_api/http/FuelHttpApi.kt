package counters.minter.sdk.minter_api.http

import com.github.kittinunf.fuel.core.HttpException
import mu.KotlinLogging


open class FuelHttpApi(httpOptions: HttpOptions) : FuelRequest {

    private val logger = KotlinLogging.logger {}
    private val httpOptions: HttpOptions
    override var headers: Map<String, String>? = null
    override var timeout: Int? = null

    private var nodeUrl: String

    init {
        this.httpOptions = httpOptions

        headers = httpOptions.headers
        nodeUrl = httpOptions.raw!!
        httpOptions.timeout?.let { timeout=it.toInt() }
    }

    fun get(
        patch: String,
        params: List<Pair<String, String>>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
    ): String? {
        try {
            val (request, response, result) = getRequest(this.nodeUrl + "/" + patch, params, timeout).responseString()
//            response.statusCode
//            println(request)
            result.fold(
                { data -> return data },
                { error ->
                    println("An error of type ${error.exception} happened: ${error.message}")
                }
            )
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> println("A network request exception was thrown: ${exception.message}")
//                is JsonMappingException -> println("A serialization/deserialization exception was thrown: ${exception.message}")
                else -> println("An exception [${exception.javaClass.simpleName}\"] was thrown")
            }
        }
        return null
    }

    fun post(
        patch: String,
        params: List<Pair<String, String>>? = null,
        tx: String? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
    ): String? {
        try {
            val txJson = if (tx != null) "{\"tx\": \"string\"}" else null
            val (request, response, result) = postRequest(this.nodeUrl + "/" + patch, params, txJson, null, timeout).responseString()
//            response.statusCode
            result.fold(
                { data -> return data },
                { error ->
                    println("An error of type ${error.exception} happened: ${error.message}")
                }
            )
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> println("A network request exception was thrown: ${exception.message}")
//                is JsonMappingException -> println("A serialization/deserialization exception was thrown: ${exception.message}")
                else -> println("An exception [${exception.javaClass.simpleName}\"] was thrown")
            }
        }
        return null
    }

}