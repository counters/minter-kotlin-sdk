package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.TransactionRequest
import counters.minter.grpc.client.TransactionResponse
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter_api.convert.ConvertTransaction
import java.util.concurrent.TimeUnit

sealed interface TransactionInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertTransaction: ConvertTransaction

/*    fun transactionGrpc2(request: TransactionRequest, deadline: Long? = null, result: ((result: TransactionResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.transaction(request, ResponseStreamObserver(request, {
            if (!success) result(null) // else  result(null)
        }) {
//                logger.info { "Async client. Stream completed. $it" }
            result(it)
            success = true
        })
    }*/

    fun transaction(hash: String, deadline: Long? = null): MinterRaw.TransactionRaw? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transaction(request, deadline)
    }

    fun transaction(request: TransactionRequest, deadline: Long? = null): MinterRaw.TransactionRaw? {

            val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
            blockingClient
                .transaction(request)?.let {
//                    logger.info { it }
                    return convertTransaction.get(it)
                } ?: run {
                return null
            }

    }

    fun transaction(hash: String, deadline: Long? = null, result: ((result: MinterRaw.TransactionRaw?) -> Unit)) {
//        if (grpcOptions != null) {
            transactionGrpc(hash, deadline) {
                if (it != null) result(convertTransaction.get(it)) else result(null)
            }
/*        } else {
            result(null)
        }*/
    }

    fun transactionGrpc(hash: String, deadline: Long? = null, result: ((result: TransactionResponse?) -> Unit)) {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transactionGrpc(request, deadline, result)
    }

    fun transactionGrpc(request: TransactionRequest, deadline: Long? = null, result: ((result: TransactionResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.transaction(request, ResponseStreamObserver(request, {
            if (!success) result(null) // else  result(null)
        }) {
//                logger.info { "Async client. Stream completed. $it" }
            result(it)
            success = true
        })
    }

    fun asyncTransaction(hash: String, deadline: Long? = null, result: ((result: MinterRaw.TransactionRaw?) -> Unit)) {
        return transactionGrpc(hash, deadline) {
            if (it != null) result(convertTransaction.get(it) ) else result(null)
        }
    }

}