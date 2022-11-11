package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.SubscribeRequest
import counters.minter.grpc.client.SubscribeResponse
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.enums.Subscribe
import counters.minter.sdk.minter_api.convert.ConvertSubscribe
import io.grpc.stub.StreamObserver
import mu.KLogger
import java.util.concurrent.TimeUnit

interface SubscribeInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertSubscribe: ConvertSubscribe

    val logger: KLogger

    fun streamSubscribeGrpc(query: String, deadline: Long? = null, result: ((result: SubscribeResponse?) -> Unit)) {
        val requestBuilder = SubscribeRequest.newBuilder()
//        if (query != null)
        requestBuilder.query = query
        val request = requestBuilder.build()
        return streamSubscribeGrpc(request, deadline, result)
    }

    fun streamSubscribeGrpc(request: SubscribeRequest, deadline: Long? = null, result: ((result: SubscribeResponse?) -> Unit)) {
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.subscribe(request, object :
            StreamObserver<SubscribeResponse?> {
            override fun onNext(response: SubscribeResponse?) {
//                logger.info { "Async client. Current weather for $request: $response" }
                if (response != null) {
                    result(response)
                } else {
                    logger.error { "Async client. Current weather for $request: $response" }
                }
            }

            override fun onError(e: Throwable) {
                logger.error { "Async client. Cannot get weather for $request : ${e.printStackTrace()}" }
                result(null)
            }

            override fun onCompleted() {
                logger.debug { "Async client. Stream completed." }
                result(null)
            }
        })
    }

    fun streamSubscribe(query: Subscribe, deadline: Long? = null, result: (result: Minter.Status?) -> Unit) {
        val request = SubscribeRequest.newBuilder().setQuery(query.str).build()
        streamSubscribeGrpc(request, deadline) {
            it?.let {
                result(convertSubscribe.status(it))
            } ?: run {
                result(null)
            }
        }
    }

    fun streamSubscribeStatus(deadline: Long? = null, result: ((result: Minter.Status?) -> Unit)) = streamSubscribe(Subscribe.TmEventNewBlock, deadline, result)

}
