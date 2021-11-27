package counters.minter.sdk.minter_api.http

import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject

open class KHttpApi(httpOptions: HttpOptions ) {

    private val logger = KotlinLogging.logger {}
    private val httpOptions: HttpOptions
//    private val client: OkHttpClient
    private var headers: Map<String, String>?

    private var nodeUrl: String
    private var timeoutD: Double = khttp.DEFAULT_TIMEOUT

    init {
        this.httpOptions = httpOptions
        httpOptions.timeout?.let { timeoutD = it }
        headers = httpOptions.headers
        nodeUrl = httpOptions.raw!!
    }

    fun asyncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
        val url = this.nodeUrl + "/" + patch
        val timeoutD = if (timeout!=null) (timeout.toDouble()/1000.0) else this.timeoutD
        val _headers = if (headers!=null) headers!! else mapOf()

        val _params = if (params == null) {
            mapOf()
        } else {
//            val newParams = mutableMapOf<String, String>()
            params.toMap()
        }
        khttp.async.get(url = url, params = _params, timeout = timeoutD, headers = _headers, onError = {
            result (null)
        }) {
            if (this.statusCode!=200) {
                error?.invoke(this.text)
            } else {
                result(this.text)
            }
        }
    }


    fun syncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
    ): String? {
        val url = this.nodeUrl + "/" + patch
        val timeoutD = if (timeout!=null) (timeout.toDouble()/1000.0) else this.timeoutD
        val _headers = if (headers!=null) headers!! else mapOf()

        val _params = if (params == null) {
            mapOf()
        } else {
            params.toMap()
        }
        val response = khttp.get(url = url, params = _params, timeout = timeoutD, headers = _headers)
        if (response.statusCode == 200) {
            return response.text
        } else if (response.statusCode == 404) {
            error?.invoke(response.text)
        } else  {
            error?.invoke(response.text)
        }
        return null
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "?")
    fun httpPost(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
//        val url = this.nodeUrl + "/" + patch
        val requestBuilder = Request.Builder()
        headers?.forEach { requestBuilder.addHeader(it.key, it.value) }
//        timeout?.let {  requestBuilder.connectTimeout(it, TimeUnit.SECONDS).readTimeout(it, TimeUnit.SECONDS) }
        val httpUrl = (this.nodeUrl + "/" + patch).toHttpUrl()
        val httpBuilder = httpUrl.newBuilder()
        params?.forEach { httpBuilder.addQueryParameter(it.first, it.second) }

        val formBuilder = FormBody.Builder()
        params?.forEach { formBuilder.add(it.first, it.second) }
        requestBuilder.url(httpUrl).post(formBuilder.build())
        logger.info { "request: ${requestBuilder.build()}" }
//        asyncOkHttp(requestBuilder.build(), timeout, error, result)
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "?")
    private fun post(
        patch: String,
        params: Map<String, String>? = null,
        timeout: Long? = null,
        notFound: ((result: JSONObject) -> Unit)? = null
    ): JSONObject? {
        val url = this.nodeUrl + "/" + patch
        val _params = if (params == null) mapOf() else params

//        val _headers =  mutableMapOf<String, String>()
//        if (headers != null) _headers.putAll(headers!!)
//        _headers.put("Content-Type", "application/json; charset=UTF-8")
        val _headers = if (headers!=null) headers!! else mapOf()
        val timeoutD = if (timeout!=null) (timeout.toDouble()/1000.0) else this.timeoutD

        val r =  khttp.post(url, params = _params, timeout = timeoutD, headers = headers!!, json = _params)
        if (r.statusCode == 200) {
            return r.jsonObject
        } else if (r.statusCode == 404) {
            notFound?.invoke(r.jsonObject)
        }
//        println("Error:" +this.nodeUrl + "/" + method.patch+", params $params respond $r r.statusCode ${r.statusCode} \n${r.jsonObject}")
//        println(r.jsonObject)
        return null
    }

}