package counters.minter.sdk.minter.help

import Config
import counters.minter.sdk.Utils
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter_api.MinterApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SerializerTest {

    private val minterApi = MinterApi(null, Config.httpOptions)
//    private val minterApi = MinterApi(Config.grpcOptions, null)

    @BeforeEach
    internal fun setUp() {
        minterApi.exception = Config.exception
    }

    @AfterEach
    internal fun tearDown() {
        minterApi.shutdown()
    }

    @Test
    fun transaction() {
        val serializer = Serializer()
        runBlocking {
            TransactionTypes.values().forEach { type ->
                Utils(Config.network).getTransactions(type, 100, true).forEach {
//                "Mt7a39b747c7b9df1600d70312d265dfc5f148df2373465ca113a08419628a4535".let {
//                        println("$type: $it")
                    minterApi.getTransactionCoroutines(it)?.let { transaction ->
//                        println("opt: ${transaction.optData}")
                        serializer.transaction(transaction)?.let { jsonObject ->
//                            println("jsonObject: $jsonObject")
                            val actualTransaction = serializer.transaction(jsonObject)
//                            println("serializer: $actualTransaction")
                            assertEquals(transaction, actualTransaction) { "Error encode from JSON to Data" }
                            assertEquals(jsonObject, serializer.transaction(actualTransaction!!)) { "Error decode from Data to JSON" }
                        } ?: run {
                            assert(false) { "Error serializer" }
                        }
                    } ?: run {
                        assert(false)
                    }

                }
            }
        }
    }

    @Test
    fun testTransaction() {
    }

    @Test
    fun testTransaction1() {
    }
}