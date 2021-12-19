package counters.minter.sdk.minter_api

import counters.minter.grpc.client.AddressRequest

interface AddressRequestInterface {

    fun getRequestAddress(address: String, height: Long? = null, delegated: Boolean? = null): AddressRequest {
        val requestBuilder = AddressRequest.newBuilder()
        height?.let { requestBuilder.setHeight(height) }
        delegated?.let { requestBuilder.setDelegated(delegated) }
        return requestBuilder.setAddress(address).build()
    }
}