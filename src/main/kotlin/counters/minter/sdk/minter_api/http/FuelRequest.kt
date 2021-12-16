package counters.minter.sdk.minter_api.http

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody

interface FuelRequest {

    var headers: Map<String, String>?
    var timeout: Int?

    fun getRequest(
        url: String,
        params: List<Pair<String, String>>? = null,
        timeout: Long? = null
    ): com.github.kittinunf.fuel.core.Request {
        val request = Fuel.get(url, params)
        headers?.let { request.header(it) }
        timeout?.let {
            request.timeout(it.toInt())
            request.timeoutRead(it.toInt())
        } ?: run {
            this.timeout?.let {
                request.timeout(it)
                request.timeoutRead(it)
            }
        }
        return request
    }

    fun postRequest(
        url: String,
        params: List<Pair<String, String>>? = null,
        json: String? = null,
        body: String? = null,
        timeout: Long? = null
    ): com.github.kittinunf.fuel.core.Request {
        val request = Fuel.get(url, params)
        headers?.let { request.header(it) }
        if (params == null) {
            json?.let { request.jsonBody(it) } ?: run {
                body?.let { request.jsonBody(it) }
            }
        }
        timeout?.let { request.timeout(it.toInt()) } ?: run { this.timeout?.let { request.timeout(it) } }
        return request
    }

/*    fun conv(fields: List<BlockField>? = null): HashSet<String>? {
        fields?.let {
            val hashSet = hashSetOf<String>()
            it.forEach {
                hashSet.add(it.name)
            }
            return hashSet
        }
        return null
    }*/

}