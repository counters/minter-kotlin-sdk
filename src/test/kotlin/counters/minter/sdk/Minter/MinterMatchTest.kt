package counters.minter.sdk.Minter

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MinterMatchTest {

    private val minterMatch = MinterMatch()
    private val testPipIn1BIP = "1000000000000000000"

    @Test
    fun getAmount() {
        assertEquals(1.0, minterMatch.getAmount(testPipIn1BIP))
    }

    @Test
    fun getPip() {
        assertEquals(testPipIn1BIP, minterMatch.getPip(1.0))
    }
}