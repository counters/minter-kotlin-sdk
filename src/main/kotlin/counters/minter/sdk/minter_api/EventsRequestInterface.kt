package counters.minter.sdk.minter_api

import counters.minter.grpc.client.EventsRequest

interface EventsRequestInterface {

    fun getRequestEvents(height: Long, search: List<String>? = null, addSymbol: Boolean = false): EventsRequest {
        val requestBuilder = EventsRequest.newBuilder()
        search?.let { requestBuilder.addAllSearch(search) }
        return requestBuilder.setHeight(height).build()
    }
}
