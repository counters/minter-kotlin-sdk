package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.TransactionResponse
import counters.minter.grpc.client.TransactionsRequest
import counters.minter.sdk.minter.enum.QueryTags
import counters.minter.sdk.minter.models.TransactionRaw
import counters.minter.sdk.minter_api.convert.ConvertTransaction
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface TransactionsInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertTransaction: ConvertTransaction
    val logger: KLogger

    fun transactionsGrpc(request: TransactionsRequest, deadline: Long? = null): List<TransactionResponse>? {
        val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
        try {
            blockingClient.transactions(request)?.let {
                return it.transactionsList
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

    fun transactions(query: Map<QueryTags, String>, page: Int = 1, perPage: Int? = null, deadline: Long? = null): List<TransactionRaw>? {
        val params = arrayListOf<String>()
        query.forEach {
            val value = if (it.value.length == 42) it.value.drop(2) else it.value
            params.add("${it.key.str}='$value'")
        }
        val strQuery = params.joinToString(" AND ")
        val requestBuilder = TransactionsRequest.newBuilder()
        if (perPage != null) requestBuilder.perPage = perPage
        val request = requestBuilder.setPage(page).setQuery(strQuery).build()
        return convert(transactionsGrpc(request, deadline))
    }

    private fun convert(list: List<TransactionResponse>?): List<TransactionRaw>? {
        list?.let {
            val _list = arrayListOf<TransactionRaw>()
            list.forEach {
                _list.add(convertTransaction.get(it))
            }
            return _list
        } ?: run {
            return null
        }
    }

    fun transactions(request: TransactionsRequest, deadline: Long? = null) = convert(transactionsGrpc(request, deadline))

}