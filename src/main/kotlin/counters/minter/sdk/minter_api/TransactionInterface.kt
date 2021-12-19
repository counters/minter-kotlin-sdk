package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.TransactionRequest
import counters.minter.grpc.client.TransactionResponse
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.convert.ConvertTransaction
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

sealed interface TransactionInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertTransaction: ConvertTransaction
    val logger: KLogger

    fun transaction(request: TransactionRequest, deadline: Long? = null): TransactionRaw? {
        val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
        try {
            blockingClient.transaction(request)?.let {
                return convertTransaction.get(it)
            } ?: run {
                return null
            }
        } catch (e: StatusRuntimeException) {
            logger.warn { e }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun transaction(hash: String, deadline: Long? = null): TransactionRaw? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transaction(request, deadline)
    }

    fun transaction(hash: String, deadline: Long? = null, result: ((result: TransactionRaw?) -> Unit)) {
        transactionGrpc(hash, deadline) {
            if (it != null) result(convertTransaction.get(it)) else result(null)
        }
    }

    fun transactionGrpc(hash: String, deadline: Long? = null, result: ((result: TransactionResponse?) -> Unit)) {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        transactionGrpc(request, deadline, result)
    }

    fun transactionGrpc(request: TransactionRequest, deadline: Long? = null, result: ((result: TransactionResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.transaction(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun asyncTransaction(hash: String, deadline: Long? = null, result: ((result: TransactionRaw?) -> Unit)) {
        return transactionGrpc(hash, deadline) {
            if (it != null) result(convertTransaction.get(it)) else result(null)
        }
    }

}