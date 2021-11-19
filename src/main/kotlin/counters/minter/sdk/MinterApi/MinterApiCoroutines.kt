package counters.minter.sdk.MinterApi

import com.google.protobuf.Empty
import counters.minter.grpc.client.*
import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterRaw
import counters.minter.sdk.MinterApi.convert.Convert
import counters.minter.sdk.MinterApi.grpc.GrpcOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

class MinterApiCoroutines(grpcOptions: GrpcOptions? = null) {

//    private var callOptions: CallOptions = CallOptions.DEFAULT
    private lateinit var stub: ApiServiceGrpcKt.ApiServiceCoroutineStub
    private var channel: ManagedChannel? = null
    private var grpcOptions: GrpcOptions

    private val convert = Convert()
    private val logger = KotlinLogging.logger {}
    private val requestEmpty = Empty.newBuilder().build()

    init {
        this.grpcOptions = grpcOptions ?: GrpcOptions()
//        this.grpcOptions = grpcOptions

        initClient()
    }

    private fun initClient() {
        val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
        if (grpcOptions.ssl_contest != null) {

        } else if (grpcOptions.useTransportSecurity) {
            channelBuilder.useTransportSecurity()
        } else {
            channelBuilder.usePlaintext()
        }
        channel = channelBuilder.build()

//        if (grpcOptions.deadline!=null) callOptions = callOptions.withDeadlineAfter(grpcOptions.deadline!!, TimeUnit.MILLISECONDS);
        if (grpcOptions.deadline != null) stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)
            .withDeadlineAfter(grpcOptions.deadline!!, TimeUnit.MILLISECONDS)
        else stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)


    }

    suspend fun getStatusGrpc(deadline: Long? = null): StatusResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else
            this.stub
        return try {
            stub.status(requestEmpty)
        } catch (e: StatusException) {
            logger.error { "StatusException: $e" }
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

    suspend fun transactionGrpc(request: TransactionRequest, deadline: Long? = null): TransactionResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.transaction(request)
        } catch (e: StatusException) {
            logger.error { "StatusException: $e" }
            null
        }
    }

    suspend fun transactionGrpc(hash: String, deadline: Long? = null): TransactionResponse? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transactionGrpc(request, deadline)
    }

    suspend fun transaction(hash: String, deadline: Long? = null): MinterRaw.TransactionRaw? {
        transactionGrpc(hash, deadline)?.let {
            return convert.transaction.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getBlockGrpc(height: Long, deadline: Long? = null): BlockResponse? {
        val request = BlockRequest.newBuilder().setHeight(height).build()
        return getBlockGrpc(request, deadline)
    }
    suspend fun getBlockGrpc(request: BlockRequest, deadline: Long? = null): BlockResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.block(request)
        } catch (e: StatusException) {
            logger.error { "StatusException: $e" }
            null
        }
    }
    suspend fun getBlock(height: Long, deadline: Long? = null): MinterRaw.BlockRaw? {
        getBlockGrpc(height, deadline)?.let {
            return convert.block.get(it)
        } ?: run {
            return null
        }
    }

}