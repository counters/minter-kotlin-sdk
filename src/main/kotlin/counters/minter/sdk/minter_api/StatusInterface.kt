package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.StatusResponse
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter_api.convert.ConvertStatus
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import mu.KLogger
import java.util.concurrent.TimeUnit

sealed interface StatusInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertStatus: ConvertStatus

    val logger: KLogger

    fun getStatusGrpc(deadline: Long? = null): StatusResponse? {
        val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
        try {
            blockingClient.status(null)?.let {
//                logger.warn { "blockingClient.status(null): ${it}" }
                return it
            } ?: run {
//                logger.warn { "blockingClient.status(null): null" }
                return null
            }
        } catch (e: StatusRuntimeException) {
            logger.warn { e }
            return null
        } catch (e: Exception) {
            logger.error { "blockingClient.status(null): ${e.message}" }
            e.printStackTrace()
            return null
        }
    }

    fun getStatus(deadline: Long? = null): Minter.Status? {
        getStatusGrpc(deadline)?.let {
            return convertStatus.get(it)
        } ?: run {
            return null
        }
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "old method")
    fun asyncStatusGrpc_Old(deadline: Long? = null, result: ((result: StatusResponse?) -> Unit)) {
        val request = null
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.status(request, object :
            StreamObserver<StatusResponse?> {
            override fun onNext(response: StatusResponse?) {
                logger.debug { "Async client. Current weather for $request: $response" }
                if (response != null) {
                    result(response)
                } else {
                    logger.error { "Async client. Current weather for $request: $response" }
                }
            }

            override fun onError(e: Throwable) {
                logger.info { "Async client. Cannot get weather for $request : ${e.printStackTrace()}" }
                result(null)
//                exitSemaphore.release()
            }

            override fun onCompleted() {
                logger.info { "Async client. Stream completed." }
//                exitSemaphore.release()
            }
        })
    }

    fun asyncStatus(deadline: Long? = null, result: ((result: Minter.Status?) -> Unit)) {
        return asyncStatusGrpc(deadline) {
            if (it != null) result(convertStatus.get(it)) else result(null)
        }
    }

    fun asyncStatusGrpc(deadline: Long? = null, result: ((result: StatusResponse?) -> Unit)) {
        val request = null
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient
            .status(request, ResponseStreamObserver(request, {
                logger.debug { "Stream completed" }
                if (!success) result(null)
            }) {
                logger.debug { "Async client. Stream completed. ${it.toString()}" }
                success = true
                result(it)
            })
    }
}
