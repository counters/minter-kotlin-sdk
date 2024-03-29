package counters.minter.sdk.minter_api

import Config
import counters.minter.sdk.Utils
import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.enums.EventTypes
import counters.minter.sdk.minter.enums.QueryTags
import counters.minter.sdk.minter.enums.SwapFromTypes
import counters.minter.sdk.minter.enums.TransactionTypes
import counters.minter.sdk.minter.help.Serializer
import counters.minter.sdk.minter.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Semaphore
import kotlin.random.Random

internal class MinterApiTest {

    private val minterHttpApi = MinterApi(null, Config.httpOptions)
    private val minterGrpcApi = MinterApi(Config.grpcOptions, null)

    private val serializer = Serializer()

    @BeforeEach
    internal fun setUp() {
        minterHttpApi.exception = Config.exception
        minterGrpcApi.exception = Config.exception
    }

    @AfterEach
    internal fun tearDown() {
        minterGrpcApi.shutdown()
        minterHttpApi.shutdown()
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
//            3279235L.let { height ->
            minterGrpcApi.getStatus()?.height?.let { height ->
                val expected = async { minterHttpApi.getBlockCoroutines(height = height/*, failed_txs = true*/) }
                val actual = async { minterGrpcApi.getBlockCoroutines(height = height/*, failed_txs = true*/) }
//                val actualValue = actual.await()
                assertNotEquals(null, actual.await())
                assertEquals(expected.await(), actual.await())
                return@runBlocking
            }
            assert(false)
        }
    }

    //        @Test
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

    //    @Test
    fun getOneTypeTransactionPoolAndOrder() {
        "Mta4dddd896893edc03aabf8440e53f154682a9090c97d85c381a51bc1d99c3998".let {
//                println(it)
            val expected = minterHttpApi.getTransaction(it)
            val actual = minterGrpcApi.getTransaction(it)
//                println(actual)
            assertNotEquals(null, actual)
            if (actual?.type == TransactionTypes.VOTE_COMMISSION.int) {
                assertVOTE_COMMISSION(expected, actual)
            } else {
                assertEquals(expected, actual)
            }
            return
        }

        assert(false)
    }

    //    @Test
    fun getOneTypeTransactionPoolAndOrderOrderNull() {
        "Mtb8d18f9d0a01e917058cc1cbaa8a8ea79ef7d6cb1d46d1bc7a6850d5880256fd".let {
//                println(it)
            val expected = minterHttpApi.getTransaction(it)
            val actual = minterGrpcApi.getTransaction(it)
//                println(actual)
            assertNotEquals(null, actual)
            if (actual?.type == TransactionTypes.VOTE_COMMISSION.int) {
                assertVOTE_COMMISSION(expected, actual)
            } else {
                assertEquals(expected, actual)
            }
            return
        }
        assert(false)
    }

    @Test
    fun getAllTypesTransactionCoroutines() {
        runBlocking {
            TransactionTypes.values().forEach { type ->
//            LibTransactionTypes.mapTypeTrs[type]?.last()?.let {
//                LibTransactionTypes.mapTypeTrs[type]?.forEach {
                Utils(Config.network).getTransactions(type, 10, true).forEach {
//                        println("$type: $it")
                    val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                    val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
                    expected.await()?.let {
                        if (type == TransactionTypes.VOTE_COMMISSION) {
                            assertVOTE_COMMISSION(it, actual.await())
                        } else if (expected.await()?.optData != null && actual.await()?.optData != null) {
                            assertWithOptData(expected.await(), actual.await())
                        } else {
                            assertEquals(it, actual.await())
                        }
                        /* if (type == TransactionTypes.VOTE_COMMISSION) {
                             println(it)
                             println(actual.await())
                         }*/
                    } ?: run {
                        assertEquals(null, actual.await())
                    }

                }
            }
        }
    }

    @Test
    fun getAllTypesFailedTransactionCoroutines() {
        runBlocking {
            TransactionTypes.values().forEach { type ->
                Utils(Config.network).getFailedTransactions(type, 10, true).forEach {
//                    println("$type: $it")
                    val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                    val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
                    expected.await()?.let {
                        if (type == TransactionTypes.VOTE_COMMISSION) {
                            assertVOTE_COMMISSION(it, actual.await())
                        } else if (expected.await()?.optData != null && actual.await()?.optData != null) {
                            assertEquals(expected.await()!!.copy(optData = null), actual.await()!!.copy(optData = null))
//                            assertEquals(expected.await()!!.copy(amount = null), actual.await()!!.copy(amount = null))
//                        assertEquals(expected.await()?.amount!!, actual.await()?.amount!!, 0.0001)
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
    fun getOneTypeTransactionCoroutines() {
        runBlocking {
            val type = TransactionTypes.MOVE_STAKE
//                "Mt1b1ba5298ce9771b58ec9bc252b9a18fc6eb301dfca585c64c44b8a63f7089f1".let {
            Utils(Config.network).getTransactions(type, 1, true).forEach {
                println(it)
                val expected = async { minterHttpApi.getTransactionCoroutines(it) }
                val actual = async { minterGrpcApi.getTransactionCoroutines(it) }
//                    val actualValue = actual.await()
                assertNotEquals(null, actual.await())
                if (type == TransactionTypes.VOTE_COMMISSION) {
                    assertVOTE_COMMISSION(expected.await(), actual.await())
                } else if (expected.await()?.optData != null && actual.await()?.optData != null) {
//                        assertEquals(expected.await()?.optData, actual.await()?.optData)
                    assertWithOptData(expected.await(), actual.await())
//                        assertEquals(expected.await()!!.copy(optData = null), actual.await()!!.copy(optData = null))
                } else {
                    assertEquals(expected.await(), actual.await())
                }
                return@runBlocking
            }
//            }
            assert(false)
        }
    }

    private fun assertWithOptData(expected: TransactionRaw?, actual: TransactionRaw?) {
        if (expected != null && actual != null) {
            if (expected.optData != null && actual.optData != null) {
//                val expectedOptData = expected.optData
//                val actualOptData = actual.optData
//            println("$expectedOptData ?== $actualOptData ")
//            println("${expectedOptData!!::class.qualifiedName}")
//            println("${actualOptData!!::class.qualifiedName}")
                assertEquals(serializer.transaction(expected), serializer.transaction(actual))
            } else {
                assertEquals(expected, actual)
            }
        } else {
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
            minterGrpcApi.getStatusCoroutines()?.let {
                val height = it.height - 1
//                transactionsByType(TransactionTypes.TypeDeclareCandidacy)?.first()?.from?.let {
//            "Mx170efb7414ba43bfbcd6aac831abc289de916635".let {
//            "Mx0903ab168597a7c86ad0d4b72424b3632be0af1b".let {
                Utils(Config.network).getExtremeDelegators(10, false).forEach {
//                println(it)
                    minterHttpApi.getAddressCoroutines(it, height, true)?.let { address ->
//                    println(address.bip_value)
//                        delay(10000)
                        minterGrpcApi.getAddressCoroutines(it, height, true)?.let {
//                        println(it.bip_value)
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
    }

    @Test
    fun getAddressCoroutinesGrpc() {
        runBlocking {
            minterGrpcApi.getStatusCoroutines()?.let {
                val height = it.height - 1
//                transactionsByType(TransactionTypes.TypeDeclareCandidacy)?.first()?.from?.let {
//            "Mx170efb7414ba43bfbcd6aac831abc289de916635".let {
//            "Mx0903ab168597a7c86ad0d4b72424b3632be0af1b".let {
                Utils(Config.network).getExtremeDelegators(10, false).forEach {
//                println(it)
                    minterHttpApi.getAddressCoroutines(it, height, true)?.let { address ->
//                    println(address.bip_value)
//                        delay(60000)
                        minterGrpcApi.getAddressCoroutines(it, height, true)?.let {
//                        println(it.bip_value)
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
                assertEquals(expectedVal.value, actualVal.value, 0.001, "$message: ${expectedVal.coin}!=${actualVal.coin}")
                assertEquals(expectedVal.bipValue, actualVal.bipValue, 0.001)
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


    @Test
    fun getAddress() {
//        "Mx170efb7414ba43bfbcd6aac831abc289de916635".let {
//        transactionsByType(TransactionTypes.TypeDeclareCandidacy)?.first()?.from?.let {
        Utils(Config.network).getNumismatistsAddresses(10, true).forEach {
//            println(it)
            minterHttpApi.getAddress(address = it, height = null, delegated = true)?.let { address ->
//                println(address)
                assertNotEquals(null, address)
                minterGrpcApi.getAddress(it, null, true)?.let {
                    assertAddress(address, it)
                    return
                } ?: run {
                    assert(false)
                }
            }
        }
        assert(false)
    }

    @Test
    fun syncAddress() {
        Utils(Config.network).getNumismatistsAddresses(10, true).forEach {
            var httpResponse: AddressRaw? = null
            var grpcResponse: AddressRaw? = null
            val semaphore = Semaphore(1)
            semaphore.acquireUninterruptibly()
            minterHttpApi.getAddress(it) {
//                println(it)
                httpResponse = it
//                assertNotEquals(null, statusResponse)
                semaphore.release()
            }
            semaphore.acquireUninterruptibly()
            minterGrpcApi.getAddress(it) {
//                println(it)
                grpcResponse = it
                semaphore.release()
            }
            semaphore.acquire()
            if (httpResponse == null || grpcResponse == null) {
                assert(false)
            } else {
                assertAddress(grpcResponse!!, httpResponse!!)
            }
        }
    }


    @Test
    fun getEventsCoroutines() {
        runBlocking {
//            EventTypes.values().forEach { type ->
            EventTypes.RemoveCandidate.let { type ->
                Utils(Config.network).getEvents(type.toOldType(), 1, false).forEach {
                    val height = it.toLong()
                    println("$type: $it")
                    minterHttpApi.getEventCoroutines(height)?.let { events ->
//                        println("HTTP: $events")
                        minterGrpcApi.getEventCoroutines(height)?.let {
//                            println("gRPC: $it")
                            assertEvents(events, it)
                        } ?: run {
                            assert(false)
                        }
                    } ?: run {
                        assert(false)
                    }
                }

            }
        }
    }

    @Test
    fun getEvents() {
        EventTypes.values().forEach { type ->
//            EventTypes.Unlock.let  { type ->
            Utils(Config.network).getEvents(type.toOldType(), 3, true).forEach {
                val height = it.toLong()
//                println("$type: $it")
                minterHttpApi.getEvent(height)?.let { events ->
//                    println(events)
                    minterGrpcApi.getEvent(height)?.let {
                        /*  if (type == EventType.RemoveCandidate) {
                              println("$type: $events")
                          }*/
//                        assertEquals(events?.count(), it.count())
//                        return@forEach
//                        assertEvents(events, it)
                    } ?: run {
                        assert(false)
                    }
                } ?: run {
                    assert(false)
                }
            }

        }
    }

    private fun assertEvents(expected: List<MinterRaw.EventRaw>, actual: List<MinterRaw.EventRaw>) {
//        println(expected)
        serializer.event(actual)?.let { json ->
            /*     serializer.event(json.asJsonArray)?.forEachIndexed { index, eventRaw ->
                     if (index > 5) return@forEachIndexed
                     println(eventRaw)
                 }*/
//            println("count: ${actual.count()} ${json.asJsonArray.count()}")
            assertEquals(expected, serializer.event(json))
        } ?: run {
            assert(false) { "Error serializer.event()" }
        }
/*        expected.forEach { expectedVal ->
            actual.firstOrNull { it.coin == expectedVal.coin }?.let { actualVal ->
                assertEquals(expectedVal.value, actualVal.value, 0.000001, "$message: ${expectedVal.coin}!=${actualVal.coin}")
                assertEquals(expectedVal.bipValue, actualVal.bipValue, 0.000001)
            } ?: run {

                assert(false) { "$message: not find coin ${expectedVal.coin} in $actual" }
            }
        }*/
    }

    @Test
    fun estimateCoinSell() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 1902
            val valueToSell: Double = 1.0
            val coinToBuy: Long = 0
            val height: Long? = it.height
            val coin_id_commission: Long? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
//            val notFoundCoin: ((notFount: Boolean) -> Unit)? = null
            val deadline: Long? = null
            minterHttpApi.estimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/)?.let { estimateCoinSell ->
//                println("HTTP: $estimateCoinSell")
                minterGrpcApi.estimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/).let {
//                    println(it)
                    assertEquals(estimateCoinSell, it)
                    return
                }
            } ?: run {
                assert(false)
            }
        }
        assert(false)
    }

    @Test
    fun asyncEstimateCoinSell() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 1902
            val valueToSell: Double = 1.0
            val coinToBuy: Long = 0
            val height: Long? = it.height
            val coin_id_commission: Long? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
            val deadline: Long? = null
            var httpResponse: Coin.EstimateCoin? = null
            var grpcResponse: Coin.EstimateCoin? = null
            val semaphore = Semaphore(1)
            semaphore.acquireUninterruptibly()
            minterHttpApi.estimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline) {
//                println("HTTP: $it")
                httpResponse = it
                semaphore.release()
            }
            semaphore.acquireUninterruptibly()
            minterGrpcApi.estimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline) {
//                println("gRPC: $it")
                grpcResponse = it
                semaphore.release()
            }
            semaphore.acquire()
            if (httpResponse == null || grpcResponse == null) {
                assert(false)
            } else {
                assertEquals(grpcResponse!!, httpResponse!!)
            }
        }
    }

    @Test
    fun estimateCoinSellCoroutines() {
        runBlocking {
            minterHttpApi.getStatus()?.let {
                val coinToSell: Long = 1902
                val valueToSell: Double = 1.0
                val coinToBuy: Long = 0
                val height: Long? = it.height
                val coin_id_commission: Long? = null
                val swap_from: SwapFromTypes? = null
                val route: List<Long>? = null
                val deadline: Long? = null
                val expected =
                    async { minterHttpApi.estimateCoinSellCoroutines(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/) }
                val actual =
                    async { minterGrpcApi.estimateCoinSellCoroutines(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/) }

                assertNotEquals(null, actual.await())
                assertEquals(expected.await(), actual.await())
                return@runBlocking
            }
            assert(false)
        }
    }

    @Test
    fun estimateCoinBuy() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 0
            val value_to_buy: Double = 1.0
            val coinToBuy: Long = 1902
            val height: Long? = it.height
            val coin_id_commission: Long? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
            val deadline: Long? = null
            minterHttpApi.estimateCoinBuy(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline)?.let { estimateCoin ->
//                println("HTTP: $estimateCoin")
                minterGrpcApi.estimateCoinBuy(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline).let {
//                    println(it)
                    assertEquals(estimateCoin, it)
                    return
                }
            } ?: run {
                assert(false)
            }
        }
        assert(false)
    }

    @Test
    fun asyncEstimateCoinBuy() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 0
            val value_to_buy: Double = 1.0
            val coinToBuy: Long = 1902
            val height: Long? = it.height
            val coin_id_commission: Long? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
            val deadline: Long? = null
            var httpResponse: Coin.EstimateCoin? = null
            var grpcResponse: Coin.EstimateCoin? = null
            val semaphore = Semaphore(1)
            semaphore.acquireUninterruptibly()
            minterHttpApi.estimateCoinBuy(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline) {
//                println("HTTP: $it")
                httpResponse = it
                semaphore.release()
            }
            semaphore.acquireUninterruptibly()
            minterGrpcApi.estimateCoinBuy(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline) {
//                println("gRPC: $it")
                grpcResponse = it
                semaphore.release()
            }
            semaphore.acquire()
            if (httpResponse == null || grpcResponse == null) {
                assert(false)
            } else {
                assertEquals(grpcResponse!!, httpResponse!!)
            }
        }
    }

    @Test
    fun estimateCoinBuyCoroutines() {
        runBlocking {
            minterHttpApi.getStatus()?.let {
                val coinToSell: Long = 0
                val value_to_buy: Double = 1.0
                val coinToBuy: Long = 1902
                val height: Long? = it.height
                val coin_id_commission: Long? = null
                val swap_from: SwapFromTypes? = null
                val route: List<Long>? = null
                val deadline: Long? = null
                val expected =
                    async { minterHttpApi.estimateCoinBuyCoroutines(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/) }
                val actual =
                    async { minterGrpcApi.estimateCoinBuyCoroutines(coinToBuy, value_to_buy, coinToSell, height, coin_id_commission, swap_from, route, deadline/*, notFoundCoin*/) }
//                println("${expected.await()} ${actual.await()}")
                assertNotEquals(null, actual.await())
                assertEquals(expected.await(), actual.await())
                return@runBlocking
            }
            assert(false)
        }
    }

    @Test
    fun estimateCoinSellAll() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 1902
            val valueToSell: Double = 1.0
            val coinToBuy: Long = 0
            val height: Long? = it.height
            val gas_price: Int? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
            val deadline: Long? = null
            minterHttpApi.estimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline/*, notFoundCoin*/)?.let { estimateCoinSell ->
//                println("HTTP: $estimateCoinSell")
                minterGrpcApi.estimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline/*, notFoundCoin*/).let {
//                    println("gRPC: $it")
                    assertEquals(estimateCoinSell, it)
                    return
                }
            } ?: run {
                assert(false)
            }
        }
        assert(false)
    }

    @Test
    fun asyncEstimateCoinSellAll() {
        minterHttpApi.getStatus()?.let {
            val coinToSell: Long = 1902
            val valueToSell: Double = 1.0
            val coinToBuy: Long = 0
            val height: Long? = it.height
            val gas_price: Int? = null
            val swap_from: SwapFromTypes? = null
            val route: List<Long>? = null
            val deadline: Long? = null
            var httpResponse: Coin.EstimateCoin? = null
            var grpcResponse: Coin.EstimateCoin? = null
            val semaphore = Semaphore(1)
            semaphore.acquireUninterruptibly()
            minterHttpApi.estimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline) {
//                println("HTTP: $it")
                httpResponse = it
                semaphore.release()
            }
            semaphore.acquireUninterruptibly()
            minterGrpcApi.estimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline) {
//                println("gRPC: $it")
                grpcResponse = it
                semaphore.release()
            }
            semaphore.acquire()
            if (httpResponse == null || grpcResponse == null) {
                assert(false)
            } else {
                assertEquals(grpcResponse!!, httpResponse!!)
            }
        }
    }

    @Test
    fun estimateCoinSellAllCoroutines() {
        runBlocking {
            minterHttpApi.getStatus()?.let {
                val coinToSell: Long = 1902
                val valueToSell: Double = 1.0
                val coinToBuy: Long = 0
                val height: Long? = it.height
                val gas_price: Int? = null
                val swap_from: SwapFromTypes? = null
                val route: List<Long>? = null
//                val notFoundCoin: ((notFount: Boolean) -> Unit)? = null
                val deadline: Long? = null
                val expected =
                    async { minterHttpApi.estimateCoinSellAllCoroutines(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline/*, notFoundCoin*/) }
                val actual =
                    async { minterGrpcApi.estimateCoinSellAllCoroutines(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline/*, notFoundCoin*/) }
//println("${expected.await()} ${actual.await()}")
                assertNotEquals(null, actual.await())
                assertEquals(expected.await(), actual.await())
                return@runBlocking
            }
            assert(false)
        }
    }

    //    @Test
    fun streamSubscribeStatusCoroutines() {
        runBlocking {
            var httpResponse: Minter.Status? = null
            var grpcResponse: Minter.Status? = null

            val jobHttp = launch {
                minterHttpApi.streamSubscribeStatusCoroutines().take(2).collect {
                    httpResponse = it
//                    println("HTTP: $it")
//                    this.cancel()
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.streamSubscribeStatusCoroutines().take(2).collect {
                    grpcResponse = it
//                    println("gRPC: $it")
//                    this.cancel()
                }
            }
            jobHttp.join()
            jobGrpc.join()
//            return@runBlocking
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }

    @Test
    fun streamSubscribe() {
        var httpResponse: Minter.Status? = null
        var grpcResponse: Minter.Status? = null

        val semaphore = Semaphore(2)
        semaphore.acquireUninterruptibly()
//        println("minterHttpApi.streamSubscribeStatus")
        minterHttpApi.streamSubscribeStatus {
//            println("HTTP: $it")
            if (httpResponse != null) {
                semaphore.release()
            }
            httpResponse = it
        }
        semaphore.acquireUninterruptibly()
//        println("minterGrpcApi.streamSubscribeStatus")
        minterGrpcApi.streamSubscribeStatus {
//            println("gRPC: $it")
            if (grpcResponse != null) {
                semaphore.release()
            }
            grpcResponse = it
//            grpcResponse = it
//            semaphore.release()
        }
        semaphore.acquire()
        grpcResponse?.let {
            assertEquals(httpResponse?.network, grpcResponse?.network)
        } ?: run {
            assert(false)
        }
    }

    @Test
    fun getSwapPool() {
        runBlocking {
            var httpResponse: MinterRaw.SwapPoolRaw? = null
            var grpcResponse: MinterRaw.SwapPoolRaw? = null
            val coin1: Long = 0
            val coin2: Long = 1902
            val height: Long? = null


            val jobHttp = launch {
                minterHttpApi.getSwapPool(coin1, coin2, height).let {
                    httpResponse = it
//                    println("HTTP: $it")
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.getSwapPool(coin1, coin2, height).let {
                    grpcResponse = it
//                    println("gRPC: $it")
                }
            }
            jobHttp.join()
            jobGrpc.join()
//            return@runBlocking
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }

    @Test
    fun asyncSwapPool() {
        var httpResponse: MinterRaw.SwapPoolRaw? = null
        var grpcResponse: MinterRaw.SwapPoolRaw? = null
        val coin1: Long = 0
        val coin2: Long = 1902
        val height: Long? = null
        val semaphore = Semaphore(1)
        semaphore.acquireUninterruptibly()
        minterHttpApi.getSwapPool(coin1, coin2, height) {
//            println("HTTP: $it")
            httpResponse = it
            semaphore.release()
        }
        semaphore.acquireUninterruptibly()
        minterGrpcApi.getSwapPool(coin1, coin2, height) {
//            println("gRPC: $it")
            grpcResponse = it
            semaphore.release()
        }
        semaphore.acquire()
        if (httpResponse == null || grpcResponse == null) {
            assert(false)
        } else {
            assertEquals(grpcResponse!!, httpResponse!!)
        }
    }

    @Test
    fun getSwapPoolCoroutines() {
        runBlocking {
            var httpResponse: MinterRaw.SwapPoolRaw? = null
            var grpcResponse: MinterRaw.SwapPoolRaw? = null
            val coin1: Long = 0
            val coin2: Long = 1902
            val height: Long? = null

            val jobHttp = launch {
                minterHttpApi.getSwapPoolCoroutines(coin1, coin2, height).let {
                    httpResponse = it
//                    println("HTTP: $it")
//                    this.cancel()
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.getSwapPoolCoroutines(coin1, coin2, height).let {
                    grpcResponse = it
//                    println("gRPC: $it")
//                    this.cancel()
                }
            }
            jobHttp.join()
            jobGrpc.join()
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }

    @Test
    fun getCoinInfo() {
        runBlocking {
            var httpResponse: MinterRaw.CoinRaw? = null
            var grpcResponse: MinterRaw.CoinRaw? = null
            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 1902 else 0
//            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 65 else 0
            val height: Long? = null


            val jobHttp = launch {
                minterHttpApi.getCoinInfo(coin, height).let {
                    httpResponse = it
//                    println("HTTP: $it")
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.getCoinInfo(coin, height).let {
                    grpcResponse = it
//                    println("gRPC: $it")
                }
            }
            jobHttp.join()
            jobGrpc.join()
//            return@runBlocking
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }

    @Test
    fun asyncCoinInfo() {
        var httpResponse: MinterRaw.CoinRaw? = null
        var grpcResponse: MinterRaw.CoinRaw? = null
        val coin: Long = if (Config.network == Utils.Network.Mainnet5) 1902 else 0
//            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 65 else 0
        val height: Long? = null
        val semaphore = Semaphore(1)
        semaphore.acquireUninterruptibly()
        minterHttpApi.getCoinInfo(coin, height) {
//            println("HTTP: $it")
            httpResponse = it
            semaphore.release()
        }
        semaphore.acquireUninterruptibly()
        minterGrpcApi.getCoinInfo(coin, height) {
//            println("gRPC: $it")
            grpcResponse = it
            semaphore.release()
        }
        semaphore.acquire()
        if (httpResponse == null || grpcResponse == null) {
            assert(false)
        } else {
            assertEquals(grpcResponse!!, httpResponse!!)
        }
    }

    @Test
    fun getCoinInfoByIdCoroutines() {
        runBlocking {
            var httpResponse: MinterRaw.CoinRaw? = null
            var grpcResponse: MinterRaw.CoinRaw? = null
            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 1902 else 0
//            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 65 else 0
            val height: Long? = null

            if (Config.network == Utils.Network.Mainnet5) {

                val jobHttp = launch {
                    minterHttpApi.getCoinInfoCoroutines(coin, height).let {
                        httpResponse = it
//                        println("HTTP: $it")
//                    this.cancel()
                    }
                }
                val jobGrpc = launch {
                    minterGrpcApi.getCoinInfoCoroutines(coin, height).let {
                        grpcResponse = it
//                        println("gRPC: $it")
//                    this.cancel()
                    }
                }
                jobHttp.join()
                jobGrpc.join()
                grpcResponse?.let {
                    assertEquals(httpResponse, grpcResponse)
                } ?: run {
                    assert(false)
                }
            }
        }
    }

    @Test
    fun getCoinInfoCoroutines() {
        runBlocking {
            var httpResponse: MinterRaw.CoinRaw? = null
            var grpcResponse: MinterRaw.CoinRaw? = null
            val coin = if (Config.network == Utils.Network.Mainnet5) "HUB" else "MNT"
//            val coin: Long = if (Config.network == Utils.Network.Mainnet5) 65 else 0
            val height: Long? = null

            if (Config.network == Utils.Network.Mainnet5) {

                val jobHttp = launch {
                    minterHttpApi.getCoinInfoCoroutines(coin, height).let {
                        httpResponse = it
//                        println("HTTP: $it")
//                    this.cancel()
                    }
                }
                val jobGrpc = launch {
                    minterGrpcApi.getCoinInfoCoroutines(coin, height).let {
                        grpcResponse = it
//                        println("gRPC: $it")
//                    this.cancel()
                    }
                }
                jobHttp.join()
                jobGrpc.join()
                grpcResponse?.let {
                    assertEquals(httpResponse, grpcResponse)
                } ?: run {
                    assert(false)
                }
            }
        }
    }

    @Test
    fun getFrozenAllCoroutines() {
        runBlocking {
            var httpResponse: List<FrozenAllRaw>? = null
            var grpcResponse:  List<FrozenAllRaw>? = null
            var startHeight: Long? = null
            var endHeight: Long = 99999999
            var addresses: List<String>? = listOf()
//            var addresses: List<String>? = listOf("Mxeee37fedf95e5ee65ce6e3ad1cbcfa9055932311")
//            var coinIds: List<Long>? = null
            var coinIds: List<Long>? = listOf(2361, 2024)
            val height: Long? = null

            val jobHttp = launch {
                minterHttpApi.getFrozenAllCoroutines(startHeight, endHeight, addresses, coinIds, height).let {
                    httpResponse = it
                    println("HTTP: $it")
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.getFrozenAllCoroutines(startHeight, endHeight, addresses, coinIds, height).let {
                    grpcResponse = it
                    println("gRPC: $it")
                }
            }
            jobHttp.join()
            jobGrpc.join()
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }

    @Test
    fun getCandidateCoroutines() {
        runBlocking {
            var httpResponse: Candidate? = null
            var grpcResponse:  Candidate? = null
            var publicKey
//            ="Mp12345bf7d1c833701ea490c2e77430486922a9fbef713b933cc3b32700f27777"
             ="Mp629b5528f09d1c74a83d18414f2e4263e14850c47a3fac3f855f200111111111"
            var notShowStakes: Boolean? = true
            val height: Long? = null

            val jobHttp = launch {
                minterHttpApi.getCandidateCoroutines(publicKey, notShowStakes, height).let {
                    httpResponse = it
//                    println("HTTP: $it")
                }
            }
            val jobGrpc = launch {
                minterGrpcApi.getCandidateCoroutines(publicKey, notShowStakes, height).let {
                    grpcResponse = it
//                    println("gRPC: $it")
                }
            }
            jobHttp.join()
            jobGrpc.join()
            grpcResponse?.let {
                assertEquals(httpResponse, grpcResponse)
            } ?: run {
                assert(false)
            }
        }
    }


    @Test
    fun getSwapPoolProvider() {
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
        val transactionType = TransactionTypes.TypeSend
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

    @Test
    fun getBestTradeCoroutines() {
        runBlocking {
            var httpResponse: BestTrade? = null
            var grpcResponse: BestTrade? = null

            if (Config.network == Utils.Network.Mainnet5) {
                val sell_coin = 1902L
                val buy_coin = 0L
                val amount = 1.0
//                val amount = 0.0000001
                val type = BestTradeType.INPUT
                val height: Long? = null
                val max_depth: Int? = null

                val jobHttp = launch {
                    minterHttpApi.getBestTradeCoroutines(sell_coin, buy_coin, amount, type, max_depth, height).let {
                        httpResponse = it
//                    println("HTTP: $it")
//                    this.cancel()
                    }
                }
                val jobGrpc = launch {
                    minterGrpcApi.getBestTradeCoroutines(sell_coin, buy_coin, amount, type, max_depth, height).let {
                        grpcResponse = it
//                    println("gRPC: $it")
//                    this.cancel()
                    }
                }
                jobHttp.join()
                jobGrpc.join()
                grpcResponse?.let {
                    assertEquals(httpResponse, grpcResponse)
                } ?: run {
                    assert(false)
                }
            }


        }
    }
}
