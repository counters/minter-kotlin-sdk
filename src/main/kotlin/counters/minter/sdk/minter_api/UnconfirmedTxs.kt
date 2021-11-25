package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.UnconfirmedTxsRequest
import counters.minter.grpc.client.UnconfirmedTxsResponse
import mu.KLogger
import java.util.concurrent.TimeUnit

interface UnconfirmedTxs {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val logger: KLogger

    fun getUnconfirmedTxsGrpc(limit: Int? = null, deadline: Long? = null): UnconfirmedTxsResponse? {
        val requestBuilder = UnconfirmedTxsRequest.newBuilder()
        if (limit != null) requestBuilder.limit = limit
        val request = requestBuilder.build()
        return getUnconfirmedTxsGrpc(request, deadline)
    }

    fun getUnconfirmedTxsGrpc(request: UnconfirmedTxsRequest?, deadline: Long? = null): UnconfirmedTxsResponse? {
        val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
        blockingClient
            .unconfirmedTxs(request).let { return it }
//            .unconfirmedTxs(null).let { return it }
    }

    fun asyncUnconfirmedTxsGrpc(request: UnconfirmedTxsRequest? = null, deadline: Long? = null, result: ((result: UnconfirmedTxsResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient

        asyncClient.unconfirmedTxs(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            logger.info { "Async client. Stream completed. $it" }
            result(it)
            success = true
        })
    }
}