package counters.minter.sdk.minter_api

import counters.minter.grpc.client.BlockField
import counters.minter.sdk.minter.Enum.QueryTags
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw.*
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import counters.minter.sdk.minter_api.http.HttpOptions
import io.grpc.ManagedChannelBuilder
import mu.KotlinLogging

class MinterApi(
    grpcOptions: GrpcOptions? = null, httpOptions: HttpOptions? = null
) {
    private val logger = KotlinLogging.logger {}
    private var minterHttpApi: MinterHttpApiOld? = null
    private var minterAsyncHttpApi: MinterAsyncHttpApi? = null
    private var minterGrpcApiCoroutines: MinterApiCoroutines? = null
    private var minterGrpcApi: MinterGrpcApi? = null

    init {
        if (grpcOptions != null) {
            val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
            if (grpcOptions.ssl_contest != null) {
//                channelBuilder.useTransportSecurity(grpcOptions.ssl_contest)
//                channelBuilder.sslContext(grpcOptions.ssl_contest)
            } else if (grpcOptions.useTransportSecurity) {
                channelBuilder.useTransportSecurity()
            } else {
                channelBuilder.usePlaintext()
            }
            minterGrpcApiCoroutines = MinterApiCoroutines(grpcOptions)
            minterGrpcApi = MinterGrpcApi(grpcOptions)
        } else if (httpOptions != null) {
            minterHttpApi = MinterHttpApiOld(httpOptions.raw!!, httpOptions.timeout, httpOptions.headers)
            minterAsyncHttpApi = MinterAsyncHttpApi(httpOptions)
        } else {
            throw Exception("grpcOptions = null && httpOptions = null")
            TODO("grpcOptions = null && httpOptions = null")
        }
    }

    fun getStatus(deadline: Long? = null): Minter.Status? {
        if (minterHttpApi!=null) {
            return minterHttpApi!!.getStatus()
        } else {
            return minterGrpcApi!!.getStatus(deadline)
        }
    }

    fun getStatus(deadline: Long? = null, result: ((result: Minter.Status?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getStatus(deadline, result)
        } ?: run {
            minterGrpcApi!!.asyncStatus(deadline, result)
        }
    }

    suspend fun getStatusCoroutines(deadline: Long? = null): Minter.Status? {
        minterAsyncHttpApi?.let {
//            return minterHttpApi!!.getStatus()
            var status: Minter.Status?=null
            val semaphore = kotlinx.coroutines.sync.Semaphore(1, 1)
            it.getStatus(deadline) {
                status = it
                semaphore.release()
            }
            semaphore.acquire()
            return status
        } ?: run {
            return minterGrpcApiCoroutines!!.getStatus(deadline)
        }
    }

    fun getBlock(height: Long, deadline: Long? = null): BlockRaw? {
        if (minterHttpApi!=null) {
            return minterHttpApi!!.getBlockRaw(height)
        } else {
            return minterGrpcApi!!.block(height, deadline)
        }
    }

    fun getBlock(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: BlockRaw?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getBlock(height, deadline, result)
        } ?: run {
            minterGrpcApi!!.asyncBlock(height, fields, failed_txs, deadline, result)
        }
    }

    suspend fun getBlockCoroutines(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null): BlockRaw? {
        minterAsyncHttpApi?.let {
//            return minterHttpApi!!.getBlockRaw(height)
            var status: BlockRaw?=null
            val semaphore = kotlinx.coroutines.sync.Semaphore(1, 1)
            it.getBlock(height, deadline) {
                status = it
                semaphore.release()
            }
            semaphore.acquire()
            return status
        } ?: run {
            return minterGrpcApiCoroutines!!.getBlock(height, fields, failed_txs, deadline)
        }
    }
    suspend fun test_getBlockCoroutines(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null): BlockRaw? {
        minterAsyncHttpApi?.let {
            return it.test_getBlock(height, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getBlock(height, fields, failed_txs, deadline)
        }
    }

    fun getTransaction(hash: String, deadline: Long? = null): TransactionRaw? {
        if (minterHttpApi!=null) {
            return minterHttpApi!!.getTransactionRaw(hash)
        } else {
            return minterGrpcApi!!.transaction(hash, deadline)
        }
    }

    fun getTransaction(hash: String, deadline: Long? = null, result: ((result: TransactionRaw?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getTransaction(hash, deadline, result)
        } ?: run {
            minterGrpcApi!!.asyncTransaction(hash, deadline, result)
        }
    }

    suspend fun getTransactionCoroutines(hash: String, deadline: Long? = null): TransactionRaw? {
        minterAsyncHttpApi?.let {
//            return minterHttpApi!!.getTransactionRaw(hash)
            var status: TransactionRaw?=null
            val semaphore = kotlinx.coroutines.sync.Semaphore(1, 1)
            it.getTransaction(hash, deadline) {
                status = it
                semaphore.release()
            }
            semaphore.acquire()
            return status
        } ?: run {
            return minterGrpcApiCoroutines!!.getTransaction(hash, deadline)
        }
    }

    fun getTransactions(
        query: Map<QueryTags, String>,
        page: Int=1,
        per_page: Int?=null,
        deadline: Long? = null
    ): List<TransactionRaw>? {
        if (minterHttpApi!=null) {
            return minterHttpApi!!.getTransactionsRaw(query, page, per_page)
        } else {
            return minterGrpcApi!!.transactions(query, page, per_page, deadline)
        }
    }

}
