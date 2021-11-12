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

    fun getStatus(deadline: Long?= null ): Minter.Status?{
        if (grpcOptions!=null) {
            getStatusGrpc(deadline)?.let {
                return convert.getStatus(it)
            } ?: run{
                return null
            }
        }
        return null
    }
    fun getStatusGrpc(deadline: Long?= null ): StatusResponse? {
        if (grpcOptions!=null) {
            blockingClient.withDeadlineAfter((deadline?: grpcOptions!!.deadline), TimeUnit.MILLISECONDS).status(null)?.let {
                return  it
            } ?: run{
                return null
            }
        }
        return null
    }

    fun asyncStatusGrpc_Old(deadline: Long?= null, result: ((result: StatusResponse?) -> Unit)) {
        val request = null
//        val exitSemaphore = Semaphore(0)
        asyncClient.withDeadlineAfter(deadline?: grpcOptions!!.deadline, TimeUnit.MILLISECONDS).status(request, object :
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
    fun asyncStatusGrpc(deadline: Long?= null, result: ((result: StatusResponse?) -> Unit)) {
        val request = null
//        val exitSemaphore = Semaphore(0)
        asyncClient.withDeadlineAfter(deadline?: grpcOptions!!.deadline, TimeUnit.MILLISECONDS)
//            .status(request, ResponseStreamObserver(request, result))
            .status(request, ResponseStreamObserver(request, {
                logger.debug { "Stream completed" }
            }) {
                logger.debug { "Async client. Stream completed. ${it.toString()}" }
                result(it)
            })
    }

    fun asyncBlockGrpc(height: Long, deadline: Long?= null): Minter.Block? {
        if (grpcOptions!=null) {
            blockingClient.withDeadlineAfter((deadline?: grpcOptions!!.deadline), TimeUnit.MILLISECONDS).block(null)?.let {
//                return  it //@TODO Convert
            } ?: run{
                return null
            }
        }
        return null
    }

    fun asyncBlockGrpc(height: Long, deadline: Long?= null, result: ((result: BlockResponse?) -> Unit)) {
        val request = BlockRequest.newBuilder().setHeight(height).build()
        return asyncBlockGrpc(request, deadline, result)
    }

    fun asyncBlockGrpc(request: BlockRequest, deadline: Long?= null, result: ((result: BlockResponse?) -> Unit)) {
        var success= false
        asyncClient.withDeadlineAfter(deadline?: grpcOptions!!.deadline, TimeUnit.MILLISECONDS)
            .block (request, ResponseStreamObserver(request, {
                if (!success) result(null)
            }) {
                logger.debug { "Async client. Stream completed. ${it.toString()}" }
                result(it)
                success=true
//                return@ResponseStreamObserver
            })
    }

    fun transaction(hash: String, deadline: Long?= null ): MinterRaw.TransactionRaw?{
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transaction(request, deadline)
    }

    fun transaction(request: TransactionRequest, deadline: Long?= null ): MinterRaw.TransactionRaw?{
        if (grpcOptions!=null) {
            blockingClient
                .withDeadlineAfter((deadline?: grpcOptions!!.deadline), TimeUnit.MILLISECONDS)
                .transaction(request).let {
                    logger.info { it }
//               return convert.getTransaction(it)
            } ?: run{
                return null
            }
        }
        return null
    }

    fun transaction(hash: String, deadline: Long?= null, result: ((result: MinterRaw.TransactionRaw?) -> Unit) ){
        if (grpcOptions!=null) {
            transactionGrpc(hash, deadline) {
                if (it!=null) result( convert.getTransaction(it)) else result(null)
            }
        } else {
            result(null)
        }
    }

    fun transactionGrpc(hash: String, deadline: Long?= null, result: ((result: TransactionResponse?) -> Unit)) {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return transactionGrpc(request, deadline, result)
    }

    fun transactionGrpc(request: TransactionRequest, deadline: Long?= null, result: ((result: TransactionResponse?) -> Unit)) {
        var success= false
        logger.info { request }
        asyncClient.withDeadlineAfter(deadline?: grpcOptions!!.deadline, TimeUnit.MILLISECONDS)
            .transaction (request, ResponseStreamObserver(request, {
                if (!success) result(null) // else  result(null)
            }) {
                logger.info { "Async client. Stream completed. $it" }
                result(it)
                success=true
            })
    }


}