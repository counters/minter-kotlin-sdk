package counters.minter.sdk.minter_api

import Config
import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.enum.QueryTags
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter.models.Commission
import counters.minter.sdk.minter.models.TransactionRaw
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
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
//        7709800L.let { height ->
            minterHttpApi.getBlock(height)?.let { block ->
                assertEquals(block.copy(transaction = arrayListOf()), minterGrpcApi.getBlock(height)?.copy(transaction = arrayListOf()))
                return
            }
        }
        assert(false)
    }

    @Test
    fun getBlockCoroutines() {
        runBlocking {
            3279235L.let { height ->
//            minterGrpcApi.getStatus()?.height.let { height ->
                val expected = async { minterHttpApi.getBlockCoroutines(height = height, failed_txs = true) }
                val actual = async { minterGrpcApi.getBlockCoroutines(height = height, failed_txs = true) }
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
        val type = TransactionTypes.REMOVE_LIMIT_ORDER
        LibTransactionTypes.mapTypeTrs[type]?.count()?.let { count ->
            val index = Random.nextInt(1, count).dec()
//            LibTransactionTypes.mapTypeTrs[type]?.getOrNull(index)?.let {
            LibTransactionTypes.mapTypeTrs[type]?.first()?.let {
//            "Mt8ec2d8b3bae4be125c8b4fd412f6733a094eea7bd08f7affcec09ecf145d34c7".let {
//                println(it)
                val expected = minterHttpApi.getTransaction(it)
                val actual = minterGrpcApi.getTransaction(it)
//                println(actual)
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
                "Mt1b1ba5298ce9771b58ec9bc252b9a18fc6eb301dfca585c64c44b8a63f7089f1".let {
//                LibTransactionTypes.mapTypeTrs[type]?.getOrNull(index)?.let {
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
    fun getAddressCoroutines() {
        runBlocking {
//            transactionsByType(TransactionTypes.TypeDeclareCandidacy)?.first()?.from?.let {
            "Mx170efb7414ba43bfbcd6aac831abc289de916635".let {
                println(it)
                minterHttpApi.getAddressCoroutines(it, 0, true)?.let { address ->
                    println(address.bip_value)
                    minterGrpcApi.getAddressCoroutines(it, 0, true)?.let {
                        println(it.bip_value)
                        assertAddress(address, it)
                        return@runBlocking
                    } ?: run {
                        assert(false)
                    }
                }
            }
            assert(false)
        }
    }

    private fun assertAddress(expected: AddressRaw, actual: AddressRaw) {
        assertEquals(expected.copy(total = listOf(), delegated = listOf(), balance = listOf()), actual.copy(total = listOf(), delegated = listOf(), balance = listOf()))
//        assertEquals(expected.balance, actual.balance)
        assertAddressBalance(expected.balance, actual.balance, "balance")
        if (expected.total != null && actual.total != null) {
            assertAddressBalance(expected.total!!, actual.total!!, "total")
        }
        if (expected.delegated != null && actual.delegated != null) {
            assertAddressDelegated(expected.delegated!!, actual.delegated!!)
        }
//        assertEquals(expected.total?.count(), actual.total?.count())
    }

    private fun assertAddressBalance(expected: List<Minter.Balance>, actual: List<Minter.Balance>, message: String = "") {
        expected.forEach { expectedVal ->
            actual.firstOrNull { it.coin == expectedVal.coin }?.let { actualVal ->
                assertEquals(expectedVal.value, actualVal.value, 0.000001, "$message: ${expectedVal.coin}!=${actualVal.coin}")
                assertEquals(expectedVal.bipValue, actualVal.bipValue, 0.000001)
            } ?: run {

                assert(false) { "$message: not find coin ${expectedVal.coin} in $actual" }
            }
        }
    }

    private fun assertAddressDelegated(expected: List<Minter.Delegated>, actual: List<Minter.Delegated>) {
        expected.forEach { expectedVal ->
            actual.firstOrNull { it.coin == expectedVal.coin }?.let { actualVal ->
                assertEquals(expectedVal.value, actualVal.value, 0.000001, "Delegated ${expectedVal.coin}!=${actualVal.coin}")
                assertEquals(expectedVal.bipValue, actualVal.bipValue, 0.000001)
            } ?: run {
                assert(false) { "Delegated: not find coin ${expectedVal.coin}" }
            }
        }
    }


    //    @Test
    fun getAddress() {
        transactionsByType(TransactionTypes.TypeDeclareCandidacy)?.first()?.let {
            println(it.from)
            minterHttpApi.getAddress(it.from)?.let { address ->
                println(address)
                minterGrpcApi.getStatus()?.let {
                    assertEquals(address, it)
                    return
                } ?: run {
                    assert(false)
                }
            }
        }
        assert(false)
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

    @Test
    fun getTransactionsRaw() {
        val transactionType = TransactionTypes.TypeDeclareCandidacy
        assertEquals(transactionType.int, transactionsByType(transactionType)?.first()?.type)
    }

    fun transactionsByType(type: TransactionTypes? = null): List<TransactionRaw>? {
        val transactionType = type ?: TransactionTypes.TypeDeclareCandidacy
        val queryMap = mutableMapOf(
            QueryTags.TagsTxType to transactionType.toHex(),
        )
        minterHttpApi.getTransactions(queryMap, 1, 1)?.let {
            if (it.isNotEmpty()) {
//                assert(true)
//                value4test.hash= it.first().hash
                return it
            }
        } ?: run {
            assert(false)
        }
        return null
    }
}