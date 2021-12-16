package counters.minter.sdk.minter_api

import com.google.protobuf.Empty
import counters.minter.grpc.client.*
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.Models.TransactionRaw
import counters.minter.sdk.minter_api.convert.Convert
import counters.minter.sdk.minter_api.convert.ConvertEvents
import counters.minter.sdk.minter_api.convert.ConvertLimitOrder
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

class MinterApiCoroutines(grpcOptions: GrpcOptions? = null) :
    LimitOrderRequestInterface,
    LimitOrdersRequestInterface,
    LimitOrdersOfPoolRequestInterface,
    EventsRequestInterface {

    //    private var callOptions: CallOptions = CallOptions.DEFAULT
    private lateinit var stub: ApiServiceGrpcKt.ApiServiceCoroutineStub
    private var channel: ManagedChannel? = null
    private var grpcOptions: GrpcOptions

    private val convert = Convert()
    private val logger = KotlinLogging.logger {}
    private val requestEmpty = Empty.newBuilder().build()

    private val convertLimitOrder = ConvertLimitOrder()

    private val convertEvents= ConvertEvents()


    init {
        this.grpcOptions = grpcOptions ?: GrpcOptions()
        initClient()
    }

    private fun initClient() {
        val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
        channelBuilder.maxInboundMessageSize(9999999)//9999999
        if (grpcOptions.ssl_contest != null) {

        } else if (grpcOptions.useTransportSecurity) {
            channelBuilder.useTransportSecurity()
        } else {
            channelBuilder.usePlaintext()
        }
        channel = channelBuilder.build()

        if (grpcOptions.deadline != null) stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)
//            .withDeadlineAfter(grpcOptions.deadline!!, TimeUnit.MILLISECONDS) // TODO(Global Deadline?!)
        else stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)
    }

    fun shutdown(): Unit {
        channel?.shutdown()
    }

    suspend fun getStatusGrpc(deadline: Long? = null): StatusResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else
            this.stub
        return try {
            stub.status(requestEmpty)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }

    }

    suspend fun getStatus(deadline: Long? = null): Minter.Status? {
        getStatusGrpc(deadline)?.let {
            return convert.getStatus(it)
        } ?: run {
            return null
        }
    }

    suspend fun getTransactionGrpc(request: TransactionRequest, deadline: Long? = null): TransactionResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.transaction(request)
        } catch (e: StatusException) {
            logger.error { "StatusException: $e" }
            null
        }
    }

    suspend fun getTransactionGrpc(hash: String, deadline: Long? = null): TransactionResponse? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return getTransactionGrpc(request, deadline)
    }

    suspend fun getTransaction(hash: String, deadline: Long? = null): TransactionRaw? {
        getTransactionGrpc(hash, deadline)?.let {
            return convert.transaction.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getBlockGrpc(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): BlockResponse? {
        val requestBuilder = BlockRequest.newBuilder().setHeight(height)
        fields?.let {
            it.forEach { requestBuilder.addFields(it) }
            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
        failed_txs?.let { requestBuilder.setFailedTxs(it) }
        val request = requestBuilder.build()
        return getBlockGrpc(request, deadline)
    }

    suspend fun getBlockGrpc(request: BlockRequest, deadline: Long? = null): BlockResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.block(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getBlock(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): MinterRaw.BlockRaw? {
        getBlockGrpc(height, fields, failed_txs, deadline)?.let {
            return convert.block.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getLimitOrderGrpc(request: LimitOrderRequest, deadline: Long? = null): LimitOrderResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrder(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrderGrpc(orderId: Long, height: Long? = null, deadline: Long? = null) = getLimitOrderGrpc(getRequestLimitOrder(orderId, height), deadline)

    suspend fun getLimitOrder(orderId: Long, height: Long? = null, deadline: Long? = null): LimitOrderRaw? {
        getLimitOrderGrpc(orderId, height, deadline).let {
            it?.let { return convertLimitOrder.get(it) } ?: run { return null }
        }
    }

    suspend fun getLimitOrdersGrpc(request: LimitOrdersRequest, deadline: Long? = null): LimitOrdersResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrders(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrdersGrpc(ids: List<Long>, height: Long? = null, deadline: Long? = null) = getLimitOrdersGrpc(getRequestLimitOrders(ids, height), deadline)

    suspend fun getLimitOrders(ids: List<Long>, height: Long?, deadline: Long?): List<LimitOrderRaw>? {
        getLimitOrdersGrpc(ids, height, deadline).let {
            it?.let { return convertLimitOrder.getList(it.ordersList) } ?: run { return null }
        }
    }

    suspend fun getLimitOrdersOfPoolGrpc(request: LimitOrdersOfPoolRequest, deadline: Long? = null): LimitOrdersOfPoolResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrdersOfPool(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrdersOfPoolGrpc(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null) =
        getLimitOrdersOfPoolGrpc(getRequestLimitOrdersOfPool(sellCoin, buyCoin, limit, height), deadline)

    suspend fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersOfPoolGrpc(sellCoin, buyCoin, limit, height, deadline).let {
            it?.let { return convertLimitOrder.getList(it.ordersList) } ?: run { return null }
        }
    }


    suspend fun getEventsGrpc(request: EventsRequest, deadline: Long? = null): EventsResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.events(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getEventsGrpc(height: Long, search: List<String>? = null, deadline: Long? = null) =
        getEventsGrpc(getRequestEvents(height, search), deadline)

    suspend fun getEvents(height: Long, search: List<String>? = null, deadline: Long? = null): List<MinterRaw.EventRaw>? {
        getEventsGrpc(height, search, deadline).let {
            it?.let { return convertEvents.get(it, height) } ?: run { return null }
        }
    }



/*    fun asyncBlockGrpc(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: BlockResponse?) -> Unit)) {
        val requestBuilder = BlockRequest.newBuilder().setHeight(height)
        fields?.let {
            it.forEach { requestBuilder.addFields(it) }
            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
        failed_txs?.let { requestBuilder.setFailedTxs(it) }
        val request = requestBuilder.build()
        asyncBlockGrpc(request, deadline, result)
    }*/

}