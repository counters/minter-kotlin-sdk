package counters.minter.sdk.minter_api.http

import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okio.IOException
import java.util.concurrent.TimeUnit

open class OkHttpApi(httpOptions: HttpOptions ) {

    private val logger = KotlinLogging.logger {}
    private val httpOptions: HttpOptions
    private val client: OkHttpClient
    private var headers: Map<String, String>?

    private var nodeUrl: String

    private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    init {
        this.httpOptions = httpOptions
        val builder = OkHttpClient.Builder()

        httpOptions.timeout?.let {
            builder.connectTimeout((it*1000.0).toLong(), TimeUnit.MILLISECONDS)
                .readTimeout((it*1000.0).toLong(), TimeUnit.MILLISECONDS)
        }
        client = builder.build()
        headers = httpOptions.headers
        nodeUrl = httpOptions.raw!!
    }

    private fun getClient(timeout: Long? = null): OkHttpClient {
        return timeout?.let {
            client
        } ?: kotlin.run {
            val builder = OkHttpClient.Builder()
            httpOptions.timeout?.let {
                builder.connectTimeout((it*1000.0).toLong(), TimeUnit.MILLISECONDS)
                    .readTimeout((it*1000.0).toLong(), TimeUnit.MILLISECONDS)
            }
            builder.build()
        }
    }

    fun asyncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
        val requestBuilder = Request.Builder()
        headers?.forEach { requestBuilder.addHeader(it.key, it.value) }
//        timeout?.let {  requestBuilder.connectTimeout(it, TimeUnit.SECONDS).readTimeout(it, TimeUnit.SECONDS) }
        val httpUrl = (this.nodeUrl + "/" + patch).toHttpUrl()
        val httpBuilder = httpUrl.newBuilder()
        params?.forEach { httpBuilder.addQueryParameter(it.first, it.second) }
        requestBuilder.url(httpUrl).get()
        logger.debug { "request: ${requestBuilder.build()}" }
        return asyncOkHttp(requestBuilder.build(), timeout, error, result)
    }

    fun syncGet(
        patch: String,
        params: List<Pair<String, String>>? = null,
//        params: Map<String, String>? = null,
        timeout: Long? = null,
//        error: ((result: String) -> Unit)? = null,
    ) : String? {
        val requestBuilder = Request.Builder()
        headers?.forEach { requestBuilder.addHeader(it.key, it.value) }
        val httpUrl = (this.nodeUrl + "/" + patch).toHttpUrl()
        val httpBuilder = httpUrl.newBuilder()
        params?.forEach { httpBuilder.addQueryParameter(it.first, it.second) }
        requestBuilder.url(httpUrl).get()
        logger.debug { "request: ${requestBuilder.build()}" }
        return syncOkHttp(requestBuilder.build(), timeout)
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
        asyncOkHttp(requestBuilder.build(), timeout, error, result)
    }


    private fun asyncOkHttp(
        request: Request,
        timeout: Long? = null,
        error: ((result: String) -> Unit)? = null,
        result: ((result: String?) -> Unit)
    ) {
        getClient(timeout).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
                result(null)
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        if(response.code != 200) {
                            error?.invoke(response.body!!.string())
                        } else {
                            result(it.body!!.string())
                        }
                    } else {
                        result(null)
                    }
                }
            }
        })
    }

    private fun syncOkHttp(
        request: Request,
        timeout: Long? = null,
//        error: ((result: String) -> Unit)? = null,
    ): String? {
        getClient(timeout).newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                return (response.body!!.string())
            } else {
                return null
//                throw IOException("Unexpected code $response")
            }
        }
    }

}