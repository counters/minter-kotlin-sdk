package counters.minter.sdk.minter_api

import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Models.Commission
import counters.minter.sdk.minter.Models.TransactionRaw
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class MinterApiTest {

    private val minterHttpApi = MinterApi(null, Config.httpOptions)
    private val minterGrpcApi = MinterApi(Config.grpcOptions, null)

    @AfterEach
    internal fun tearDown() {
        minterGrpcApi.shutdown()
    }

    @Test
    fun getStatus() {
        minterHttpApi.getStatus()?.let { status ->
            minterGrpcApi.getStatus()?.let {
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
    fun getOneTypeTransaction() {
        val type = TransactionTypes.VOTE_COMMISSION
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
                if (type == TransactionTypes.VOTE_COMMISSION) {
                    assertVOTE_COMMISSION(expected, actual)
                } else {
                    assertEquals(expected, actual)
                }
                return
            }
        }
        assert(false)
    }

    @Test
    fun getAllTypesTransactionCoroutines() {
        runBlocking {
//        val type = TransactionTypes.TypeSend
            TransactionTypes.values().forEach { type ->
//                val type = TransactionTypes.TypeBuyCoin
//            LibTransactionTypes.mapTypeTrs[type]?.last()?.let {
                LibTransactionTypes.mapTypeTrs[type]?.forEach {
                    val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                    val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
                    expected.await()?.let {
                        if (type == TransactionTypes.VOTE_COMMISSION) {
                            assertVOTE_COMMISSION(it, actual.await())
                        } else {
                            assertEquals(it, actual.await())
                        }
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
                    if (type == TransactionTypes.VOTE_COMMISSION) {
                        assertVOTE_COMMISSION(expected.await(), actual.await())
                    } else {
                        assertEquals(expected.await(), actual.await())
                    }
                    return@runBlocking
                }
            }
            assert(false)
        }
    }

    private fun assertVOTE_COMMISSION(expected: TransactionRaw?, actual: TransactionRaw?) {
        actual?.let {
            (it.optData as List<Commission>).forEach { commision ->
                val expectedValue = (expected?.optData as List<Commission>).firstOrNull { it.key == commision.key }
                if (expectedValue != null) {
                    assertEquals(expectedValue.value, commision.value, 0.00000000000001)
                } else {
                    assert(false)
                }
            }
        } ?: run { assert(false) }
        assertEquals(expected?.copy(optData = null), actual?.copy(optData = null))
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