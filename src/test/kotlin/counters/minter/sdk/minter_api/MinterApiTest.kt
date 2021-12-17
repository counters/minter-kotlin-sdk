package counters.minter.sdk.minter_api

import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import counters.minter.sdk.minter_api.http.HttpOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class MinterApiTest {

    private val hostname = "node.knife.io"

    //    private val hostname = "node-api.testnet.minter.network"
//    private val hostname = "node-api.taconet.minter.network"
    private val httpOptions = HttpOptions(raw = "http://$hostname:8843/v2", timeout = 60000)
    private val grpcOptions = GrpcOptions(hostname = hostname, deadline = 1000)

    //    private val minterApi = MinterApi(grpcOptions, httpOptions)
    private val minterHttpApi = MinterApi(null, httpOptions)
    private val minterGrpcApi = MinterApi(grpcOptions, null)

    @AfterEach
    internal fun tearDown() {
        minterGrpcApi.shutdown()
    }

    @Test
    fun getStatus() {
        minterHttpApi.getStatus()?.let { status ->
            minterGrpcApi.getStatus()?.let {
//                assert(false)
                assertEquals(status, it)
                return
            } ?: run {
                assert(false)
            }
        }
        assert(false)
    }

    @Test
    fun getStatusCoroutines() {
        runBlocking {
            assertNotEquals(null, minterHttpApi.getStatusCoroutines())
        }
    }

    @Test
    fun getBlock() {
        minterHttpApi.getStatus()?.height?.let { height ->
            minterHttpApi.getBlock(height)?.let { block ->
                assertEquals(block, minterGrpcApi.getBlock(height))
                return
            }
        }
        assert(false)
    }

    @Test
    fun getBlockCoroutines() {
        runBlocking {
            minterGrpcApi.getStatus()?.let { status ->
                val expected = async { minterHttpApi.getBlockCoroutines(status.height) }
                val actual = async { minterGrpcApi.getBlockCoroutines(status.height) }
//                val actualValue = actual.await()
                assertNotEquals(null, actual.await())
                assertEquals(expected.await(), actual.await())
                return@runBlocking
            }
            assert(false)
        }
    }

    @Test
    fun getTransaction() {
        val type = TransactionTypes.CREATE_TOKEN
        LibTransactionTypes.mapTypeTrs[type]?.count()?.let { count ->
            val index = Random.nextInt(1, count).dec()
//            LibTransactionTypes.mapTypeTrs[type]?.getOrNull(index)?.let {
            LibTransactionTypes.mapTypeTrs[type]?.first()?.let {
//            "Mt8ec2d8b3bae4be125c8b4fd412f6733a094eea7bd08f7affcec09ecf145d34c7".let {
                println(it)
                val expected = minterHttpApi.getTransaction(it)
                val actual = minterGrpcApi.getTransaction(it)
                println(actual)
                assertNotEquals(null, actual)
                assertEquals(expected, actual)
                return
            }
        }
        assert(false)
    }

    @Test
    fun getTransactionCoroutines() {
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
    fun getTransactionCoroutines2() {
        runBlocking {
            val type = TransactionTypes.TypeSend
            LibTransactionTypes.mapTypeTrs[type]?.count()?.let { count ->
                val index = Random.nextInt(1, count).dec()
                LibTransactionTypes.mapTypeTrs[type]?.getOrNull(index)?.let {
                    val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                    val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
//                    val actualValue = actual.await()
                    assertNotEquals(null, actual.await())
                    assertEquals(expected.await(), actual.await())
                    return@runBlocking
                }
            }
            assert(false)
        }
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