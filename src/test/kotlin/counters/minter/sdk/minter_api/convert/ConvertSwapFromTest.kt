package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.SwapFrom
import counters.minter.sdk.minter.enum.SwapFromTypes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConvertSwapFromTest {

    private val convertSwapFrom = ConvertSwapFrom()

    @Test
    fun convSwapFrom() {
        SwapFromTypes.values().forEach {
            assertEquals(it.value, convertSwapFrom.convSwapFrom(it).name)
        }
    }
    @Test
    fun convSwapFromGrpc() {
        SwapFrom.values().forEach {
            assertEquals(it.name, convertSwapFrom.convSwapFrom(it)?.value)
        }
    }

    @Test
    fun testConvSwapFrom() {
    }
}