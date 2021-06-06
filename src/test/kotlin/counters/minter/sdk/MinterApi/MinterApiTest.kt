package counters.minter.sdk.MinterApi

import counters.minter.sdk.Minter.Enum.QueryTags
import counters.minter.sdk.Minter.Enum.TransactionTypes
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MinterApiTest {

    private val minterApi = MinterApi("http://127.0.0.1:8843/v2", 30.0)
    val testAddress = "Mx0000000000000000000000000000000000000000"

    @Test
    fun getAddress() {

        val result = minterApi.getAddress(testAddress)
        if (result != null) {
            assertEquals(testAddress, result.address)
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

    @Test
    fun getTransactionsRaw() {
        val queryMap = mutableMapOf(
            QueryTags.TagsTxType to TransactionTypes.TypeMultiSend.toHex(),
        )
        minterApi.getTransactionsRaw(queryMap, 1,2)?.let {
            if (it.count()>1) assert(true)
        } ?: run{
            assert(false)
        }
    }

}