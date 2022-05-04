package counters.minter.sdk.minter.help

import Config
import counters.minter.sdk.Utils
import counters.minter.sdk.minter.enum.EventTypes
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter_api.MinterApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SerializerTest {

    //    private val minterApi = MinterApi(null, Config.httpOptions)
    private val minterApi = MinterApi(Config.grpcOptions, null)

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
                Utils(Config.network).getTransactions(type, 10, true).forEach {
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
    fun event() {
        val serializer = Serializer()
        runBlocking {
//            EventTypes.values().forEach { type ->
            EventTypes.OrderExpired.let { type ->
                Utils(Config.network).getEvents(type.toOldType(), 1, true).forEach { height ->
//                "12345".let {
//                    println("$type: height: $height")
//                    minterApi.getEventCoroutines(height.toLong())?.forEachIndexed {  index, eventRaw ->
                    minterApi.getEventCoroutines(height.toLong())?.let { eventsRaw ->
                        val jsonArray = serializer.event(eventsRaw)
/*                        serializer.event( jsonArray!!.asJsonArray)?.forEachIndexed { index, eventRaw ->
                            if (index>5) return@forEachIndexed
                            println(eventRaw)
                        }
                        println(eventsRaw.count())*/
                        assertEquals(eventsRaw, serializer.event(jsonArray!!.asJsonArray))
                        var i = 0
                        eventsRaw.forEach { eventRaw ->
                            if (type.data.name == eventRaw.type) {
                                if (i >= 1) return@forEach
//                            println("opt: ${eventRaw.option}")
                                serializer.event(eventRaw)?.let { jsonObject ->
//                            println("jsonObject: $jsonObject")
                                    val actualEvent = serializer.event(jsonObject)
//                            println("serializer: $actualEvent")
                                    assertEquals(eventRaw, actualEvent) { "Error encode from JSON to Data" }
                                    assertEquals(jsonObject, serializer.event(actualEvent!!)) { "Error decode from Data to JSON" }
                                } ?: run {
                                    assert(false) { "Error serializer" }
                                }
                                i++
                            }
                        }
                    } ?: run {
                        assert(false)
                    }
                }
            }
        }
    }
}