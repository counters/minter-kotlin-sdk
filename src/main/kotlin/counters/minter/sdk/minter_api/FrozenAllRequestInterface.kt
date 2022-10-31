package counters.minter.sdk.minter_api

import counters.minter.grpc.client.FrozenAllRequest

interface FrozenAllRequestInterface {

    fun getRequestFrozenAll(startHeight: Long? = null, endHeight: Long, addresses: List<String>? = null, coinIds: List<Long>? = null, height: Long? = null): FrozenAllRequest {
        val requestBuilder = FrozenAllRequest.newBuilder()
        startHeight?.let { requestBuilder.setStartHeight(it) }
        height?.let { requestBuilder.setHeight(it) }
        addresses?.let { requestBuilder.addAllAddresses(it) }
        coinIds?.let { requestBuilder.addAllCoinIds(it) }
        return requestBuilder.setEndHeight(endHeight).build()
    }
}
