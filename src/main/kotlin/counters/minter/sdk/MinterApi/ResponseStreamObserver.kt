package counters.minter.sdk.MinterApi

import io.grpc.stub.StreamObserver
import mu.KotlinLogging

class ResponseStreamObserver <R, T>(
    private val request: T?,
    private val completed: ((result: Boolean) -> Unit)?=null,
    private val result: ((result: R?) -> Unit),
) : StreamObserver<R?> {
    private val logger = KotlinLogging.logger {}
    override fun onNext(response: R?) {
        logger.debug { "Async client. Current for $request: $response" }
        if (response != null) {
            result(response)
        } else {
            logger.error { "Async client. Current for $request: $response" }
        }
    }
    override fun onError(e: Throwable) {
//            ${e.printStackTrace()}
        logger.error { "Async client. Cannot get for $request :  ${e.message}" }
        result(null)
        completed?.let { it(false) }
    }
    override fun onCompleted() {
        logger.debug { "Async client. Stream completed." }
        completed?.let { it(true) }
    }
}