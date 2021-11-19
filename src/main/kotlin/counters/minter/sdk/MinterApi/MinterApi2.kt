package counters.minter.sdk.MinterApi

import counters.minter.grpc.client.*
import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterRaw
import counters.minter.sdk.MinterApi.convert.Convert
import counters.minter.sdk.MinterApi.grpc.GrpcOptions
import counters.minter.sdk.MinterApi.http.HttpOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

class MinterApi2(grpcOptions: GrpcOptions? = null, httpOptions: HttpOptions? = null) {

    private var channel: ManagedChannel? = null
    private var grpcOptions: GrpcOptions? = null
    private var httpOptions: HttpOptions? = null

    private var asyncClient: ApiServiceGrpc.ApiServiceStub
    private var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    private val convert = Convert()

    private val logger = KotlinLogging.logger {}

    init {
        if (grpcOptions != null) {
            this.grpcOptions = grpcOptions

            val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
            if (grpcOptions.ssl_contest != null) {
//                channelBuilder.useTransportSecurity(grpcOptions.ssl_contest)
//                channelBuilder.sslContext(grpcOptions.ssl_contest)
            } else if (grpcOptions.useTransportSecurity) {
                channelBuilder.useTransportSecurity()
            } else {
                channelBuilder.usePlaintext()
            }
            channel = channelBuilder.build()
        } else if (httpOptions != null) this.httpOptions = httpOptions
        else {
            throw Exception("grpcOptions = null && httpOptions = null")
        }
        blockingClient = ApiServiceGrpc.newBlockingStub(channel)
        asyncClient = ApiServiceGrpc.newStub(channel)
    }

    fun getStatus(deadline: Long? = null): Minter.Status? {
        if (grpcOptions != null) {
            getStatusGrpc(deadline)?.let {
                return convert.getStatus(it)
            } ?: run {
                return null
            }
        }
        return null
    }

    fun getStatusGrpc(deadline: Long? = null): StatusResponse? {
        if (grpcOptions != null) {
            val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
            blockingClient.status(null)?.let {
                return it
            } ?: run {
                return null
            }
        }
        return null
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
            if (it != null) result(convert.getStatus(it)) else result(null)
        }
    }

    fun asyncStatusGrpc(deadline: Long? = null, result: ((result: StatusResponse?) -> Unit)) {
        val request = null
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient
            .status(request, ResponseStreamObserver(request, {
                logger.debug { "Stream completed" }
            }) {
                logger.debug { "Async client. Stream completed. ${it.toString()}" }
                result(it)
            })
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "TODO Convert")
    fun block(height: Long, deadline: Long? = null): MinterRaw.BlockRaw? {
        if (grpcOptions != null) {
            blockGrpc(height, deadline)?.let {
                return convert.block.get(it)
            } ?: run {
                return null
            }
        }
        return null
    }

    fun blockGrpc(height: Long, deadline: Long? = null): BlockResponse? {
        val request = BlockRequest.newBuilder().setHeight(height).build()
        return blockGrpc(request, deadline)
    }

    fun blockGrpc(request: BlockRequest, deadline: Long? = null): BlockResponse? {
        if (grpcOptions != null) {
            val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
            blockingClient.block(request)?.let {
                return it
            } ?: run {
                return null
            }
        }
        return null
    }

    /**
     * fields=transactions&fields=missed&fields=block_reward&fields=size&fields=proposer&fields=validators&fields=evidence
     * &failed_txs=true
     * */
    fun asyncBlockGrpc(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: BlockResponse?) -> Unit)) {
        val requestBuilder = BlockRequest.newBuilder().setHeight(height)
        fields?.let {
            it.forEach { requestBuilder.addFields(it) }
            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
//        requestBuilder.addFields(BlockField.transactions)
//        requestBuilder.addFields(BlockField.transactions)
//        requestBuilder.addFieldsValue(BlockField.transactions.number)
        failed_txs?.let { requestBuilder.setFailedTxs(it) }
        val request = requestBuilder.build()
        asyncBlockGrpc(request, deadline, result)
    }

    fun asyncBlockGrpc(request: BlockRequest, deadline: Long? = null, result: ((result: BlockResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.block(request, ResponseStreamObserver(request, {
            if (!success) result(null)
            }) {
                result(it)
                success = true
//                return@ResponseStreamObserver
            })
    }

    fun asyncBlock(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: MinterRaw.BlockRaw?) -> Unit)) {
        asyncBlockGrpc(height, fields, failed_txs, deadline) {
            it?.let {
                result(convert.block.get(it))
            } ?: run {
                result(null)
            }
        }
    }

    fun transaction(hash: String, deadline: Long? = null): MinterRaw.TransactionRaw? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transaction(request, deadline)
    }

    fun transaction(request: TransactionRequest, deadline: Long? = null): MinterRaw.TransactionRaw? {
        if (grpcOptions != null) {
            val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
            blockingClient
                .transaction(request)?.let {
//                    logger.info { it }
                    return convert.getTransaction(it)
                } ?: run {
                return null
            }
        }
        return null
    }

    fun transaction(hash: String, deadline: Long? = null, result: ((result: MinterRaw.TransactionRaw?) -> Unit)) {
        if (grpcOptions != null) {
            transactionGrpc(hash, deadline) {
                if (it != null) result(convert.getTransaction(it)) else result(null)
            }
        } else {
            result(null)
        }
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

    /**
     *  tm.event = 'NewBlock' or tm.event = 'Tx'

     * */
    fun streamSubscribeGrpc(query: String, deadline: Long? = null, result: ((result: SubscribeResponse?) -> Unit)) {
        val requestBuilder = SubscribeRequest.newBuilder()
//        if (query != null)
        requestBuilder.query = query
        val request = requestBuilder.build()
        return streamSubscribeGrpc(request, deadline, result)
    }

    fun streamSubscribeGrpc(request: SubscribeRequest, deadline: Long? = null, result: ((result: SubscribeResponse?) -> Unit)) {
//        val streamClient = ApiServiceGrpc.newStub(channel)
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.subscribe(request, object :
                StreamObserver<SubscribeResponse?> {
                override fun onNext(response: SubscribeResponse?) {
                    logger.info { "Async client. Current weather for $request: $response" }
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
                    logger.info { "Async client. Stream completed." }
                    result(null)
                }
            })
    }

//    fun _subscribe(request: SubscribeRequest, responseObserver: StreamObserver<SubscribeResponse>?) {
//        channel.newCall()
//        ClientCalls.asyncServerStreamingCall(
//            channel
//                .newCall<SubscribeRequest, SubscribeResponse>(ApiServiceGrpc.METHOD_SUBSCRIBE, this.getCallOptions()),
//            request,
//            responseObserver
//        )
//    }


}