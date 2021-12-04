package counters.minter.sdk.minter_api

import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import counters.minter.sdk.minter_api.http.HttpOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MinterApiTest {

    private val httpOptions = HttpOptions(raw = "http://node.knife.io:8843/v2", timeout = 1.0)
    private val grpcOptions = GrpcOptions(hostname = "node.knife.io", deadline = 1000)

//    private val minterApi = MinterApi(grpcOptions, httpOptions)
    private val minterHttpApi = MinterApi(null, httpOptions)
    private val minterGrpcApi = MinterApi(grpcOptions, null)


    @Test
    fun getStatus() {
    }

    @Test
    fun testGetStatus() {
    }

    @Test
    fun getStatusCoroutines() {
    }

    @Test
    fun getBlock() {
    }

    @Test
    fun testGetBlock() {
    }

    @Test
    fun getBlockCoroutines() {
    }

    @Test
    fun getTransaction() {
    }

    @Test
    fun getTransaction2() {
        runBlocking {
//        val type = TransactionTypes.TypeSend
            TransactionTypes.values().forEach { type ->
//                val type = TransactionTypes.TypeBuyCoin
//            LibTransactionTypes.mapTypeTrs[type]?.last()?.let {
                LibTransactionTypes.mapTypeTrs[type]?.forEach {
                    val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                    val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
                    expected.await()?.let {
             //           println(it)
                        assertEquals(it, actual.await())
                    } ?: run {
                        assertEquals(null, actual.await())
                    }

                }
            }
        }
    }

    @Test
    fun testGetTransaction() {
    }

    @Test
    fun getTransactionCoroutines() {
    }

    @Test
    fun getTransactions() {
    }

    @Test
    fun getLimitOrder() {
    }

    @Test
    fun testGetLimitOrder() {
    }

    @Test
    fun getLimitOrders() {
    }

    @Test
    fun testGetLimitOrders() {
    }

    @Test
    fun getLimitOrdersOfPool() {
    }

    @Test
    fun testGetLimitOrdersOfPool() {
    }

    @Test
    fun getLimitOrdersCoroutines() {
    }

    @Test
    fun getLimitOrderCoroutines() {
    }

    @Test
    fun getLimitOrdersOfPoolCoroutines() {
    }
}