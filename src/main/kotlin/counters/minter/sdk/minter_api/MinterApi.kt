package counters.minter.sdk.minter_api

import counters.minter.grpc.client.BlockField
import counters.minter.sdk.minter.Enum.QueryTags
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw.*
import counters.minter.sdk.minter.Models.TransactionRaw
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
    private var minterCoroutinesHttpApi: MinterCoroutinesHttpApi? = null

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
            val timeoutD = if (httpOptions.timeout != null) httpOptions.timeout.toDouble() / 1000.0 else null
            minterHttpApi = MinterHttpApiOld(httpOptions.raw!!, timeoutD, httpOptions.headers)
            minterAsyncHttpApi = MinterAsyncHttpApi(httpOptions)
            minterCoroutinesHttpApi = MinterCoroutinesHttpApi(httpOptions = httpOptions)
        } else {
            throw Exception("grpcOptions = null && httpOptions = null")
            TODO("grpcOptions = null && httpOptions = null")
        }
    }

    fun shutdown() {
        minterGrpcApiCoroutines?.shutdown()
        minterGrpcApi?.shutdown()
    }

    fun getStatus(deadline: Long? = null): Minter.Status? {
        if (minterHttpApi != null) {
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
        minterCoroutinesHttpApi?.let {
            return it.getStatus(deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getStatus(deadline)
        }
    }

    fun getBlock(height: Long, deadline: Long? = null): BlockRaw? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getBlockRaw(height)
        } else {
            return minterGrpcApi!!.block(height, deadline)
        }
    }

    fun getBlock(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null, result: ((result: BlockRaw?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getBlock(height, deadline, result)
        } ?: run {
            minterGrpcApi!!.asyncBlock(height, fields, failed_txs, deadline, result)
        }
    }

    suspend fun getBlockCoroutines(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): BlockRaw? {
        minterCoroutinesHttpApi?.let {
            return it.getBlock(height, minterCoroutinesHttpApi!!.BlockFieldHashSet(fields), failed_txs, null, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getBlock(height, fields, failed_txs, deadline)
        }
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "for test")
    suspend fun test_getBlockCoroutines(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): BlockRaw? {
        minterAsyncHttpApi?.let {
            return it.test_getBlock(height, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getBlock(height, fields, failed_txs, deadline)
        }
    }

    fun getTransaction(hash: String, deadline: Long? = null): TransactionRaw? {
        if (minterHttpApi != null) {
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
        minterCoroutinesHttpApi?.let {
            return it.getTransaction(hash, deadline)
/*        }
        minterAsyncHttpApi?.let {
//            return minterHttpApi!!.getTransactionRaw(hash)
            var status: TransactionRaw?=null
            val semaphore = kotlinx.coroutines.sync.Semaphore(1, 1)
            it.getTransaction(hash, deadline) {
                status = it
                semaphore.release()
            }
            semaphore.acquire()
            return status*/
        } ?: run {
            return minterGrpcApiCoroutines!!.getTransaction(hash, deadline)
        }
    }

    fun getTransactions(
        query: Map<QueryTags, String>,
        page: Int = 1,
        per_page: Int? = null,
        deadline: Long? = null
    ): List<TransactionRaw>? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getTransactionsRaw(query, page, per_page)
        } else {
            return minterGrpcApi!!.transactions(query, page, per_page, deadline)
        }
    }

    fun getLimitOrder(orderId: Long, height: Long? = null, deadline: Long? = null): LimitOrderRaw? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getLimitOrder(orderId, height, deadline)
        } else {
            return minterGrpcApi!!.getLimitOrder(orderId, height, deadline)
        }
    }

    fun getLimitOrder(orderId: Long, height: Long? = null, deadline: Long? = null, result: ((result: LimitOrderRaw?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getLimitOrder(orderId, height, deadline, result)
        } ?: run {
            minterGrpcApi!!.getLimitOrder(orderId, height, deadline, result)
        }
    }

    fun getLimitOrders(ids: List<Long>, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getLimitOrders(ids, height, deadline)
        } else {
            return minterGrpcApi!!.getLimitOrders(ids, height, deadline)
        }
    }

    fun getLimitOrders(ids: List<Long>, height: Long? = null, deadline: Long? = null, result: ((result: List<LimitOrderRaw>?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getLimitOrders(ids, height, deadline, result)
        } ?: run {
            minterGrpcApi!!.getLimitOrders(ids, height, deadline, result)
        }
    }

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height)
        } else {
            return minterGrpcApi!!.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height, deadline)
        }
    }

    fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null, result: ((result: List<LimitOrderRaw>?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height, deadline, result)
        } ?: run {
            minterGrpcApi!!.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height, deadline, result)
        }
    }

    suspend fun getLimitOrdersCoroutines(ids: List<Long>, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        minterCoroutinesHttpApi?.let {
            return it.getLimitOrders(ids, height, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getLimitOrders(ids, height, deadline)
        }
    }

    suspend fun getLimitOrderCoroutines(orderId: Long, height: Long? = null, deadline: Long? = null): LimitOrderRaw? {
        minterCoroutinesHttpApi?.let {
            return it.getLimitOrder(orderId, height, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getLimitOrder(orderId, height, deadline)
        }
    }

    suspend fun getLimitOrdersOfPoolCoroutines(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        minterCoroutinesHttpApi?.let {
            return it.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getLimitOrdersOfPool(sellCoin, buyCoin, limit, height, deadline)
        }
    }

    fun getEvent(height: Long, search: List<String>? = null/*, addSymbol: Boolean = false*/, deadline: Long? = null): List<EventRaw>? {
        if (minterHttpApi != null) {
            return minterHttpApi!!.getEventsRaw(height, search)
        } else {
            return minterGrpcApi!!.getEvents(height, search, deadline)
        }
    }

    fun getEvent(height: Long, search: List<String>? = null, deadline: Long? = null, result: ((result: List<EventRaw>?) -> Unit)) {
        minterAsyncHttpApi?.let {
            it.getEvents(height, search, deadline, result)
        } ?: run {
            minterGrpcApi!!.getEvents(height, search, deadline, result)
        }
    }

    suspend fun getEventCoroutines(height: Long, search: List<String>? = null, deadline: Long? = null): List<EventRaw>? {
        minterCoroutinesHttpApi?.let {
            return it.getEvents(height, search, deadline)
        } ?: run {
            return minterGrpcApiCoroutines!!.getEvents(height, search, deadline)
        }
    }

}
