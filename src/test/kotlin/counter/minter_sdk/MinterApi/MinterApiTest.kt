package counter.minter_sdk.MinterApi

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MinterApiTest {

    private val minterApi = MinterApi("http://127.0.0.1:8843/v2", 30.0)

    @Test
    fun getAddress() {
        val address = "Mx0000000000000000000000000000000000000000"
        val result = minterApi.getAddress(address)
        if (result != null) {
            assertEquals(address, result.address)
        } else {
            assert(false)
        }
    }

    @Test
    fun getStatus() {
        assertNotEquals(null, minterApi.getStatus())
    }

    @Test
    fun getMinGasPrice() {
        assertTrue {
            minterApi.getMinGasPrice()!! >0
        }
    }

}