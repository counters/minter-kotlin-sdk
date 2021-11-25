package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.Enum.QueryTags
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MinterHttpApiOldTest {

    private val hostname = "localhost"
    private val minterHttpApiOld = MinterHttpApiOld("http://$hostname:8843/v2", 300.0)
    private val _testAddress = "Mx0000000000000000000000000000000000000000"

//    private var status: Minter.Status? = null
//    private var address: String? = null

    data class Value4test(
        var init: Boolean = false,
        var status: Minter.Status? = null,
        var address: String? = null,
        var hash: String? = null,
//        var status: Minter.Status? = null,
    )
    private val value4test = Value4test()

    init {
//        if (!value4test.init) loadValuesForMethod()


    }

    private fun loadValuesForMethod(httpMethod: HttpMethod? = null){
        val method = if (httpMethod==null) HttpMethod.values() else arrayOf(httpMethod)
        method.forEach {
            if (it== HttpMethod.STATUS) value4test.status = minterHttpApiOld.getStatus()
            if (it== HttpMethod.ADDRESS) value4test.address = _getTransactionsRaw(TransactionTypes.TypeSend)!!.first().from
            if (it== HttpMethod.TRANSACTION) value4test.hash = _getTransactionsRaw(TransactionTypes.TypeSend)!!.first().hash
        }
        println(value4test)
    }

    @Test
    fun getAddress(/*address: String? = null*/) {
//        val testAddress = address ?: value4test.address
        val address: String = if (value4test.address!=null ) value4test.address!! else this._getTransactionsRaw(TransactionTypes.TypeSend)!!.first().from
        val result = minterHttpApiOld.getAddress(address)
        if (result != null) {
            assertEquals(address, result.address)
        } else {
            assert(false)
        }
    }

    @Test
    fun getStatus() {
        assertNotEquals(null, minterHttpApiOld.getStatus())
        assertNotEquals(null, minterHttpApiOld.getStatus())
    }

    @Test
    fun getMinGasPrice() {
        assertTrue {
            minterHttpApiOld.getMinGasPrice()!! >0
        }
    }

    @Test
    fun getTransactionRaw(/*transaction: String? = null*/) {
//        val hash = transaction ?: value4test.hash!!
        val hash: String = if (value4test.hash!=null ) value4test.hash!! else this._getTransactionsRaw(TransactionTypes.TypeSend)!!.first().hash

        minterHttpApiOld.getTransactionRaw(hash)?.let {
            if (it.hash == hash  ) assert(true)
        } ?: run{
            assert(false)
        }
//        assert(false)
    }

//    @Test
    fun getTransactionsRaw(type: TransactionTypes?= null): MinterRaw.TransactionRaw? {
        val transactionType = type ?: TransactionTypes.TypeSend
        val queryMap = mutableMapOf(
            QueryTags.TagsTxType to transactionType.toHex(),
        )
        println(queryMap)
        minterHttpApiOld.getTransactionsRaw(queryMap, 1,1)?.let {
            if (it.isNotEmpty()) {
                assert(true)
                value4test.hash= it.first().hash
                return it.first()
            }
        } ?: run{
            assert(false)
        }
        return null
    }
    fun _getTransactionsRaw(type: TransactionTypes?= null): List<MinterRaw.TransactionRaw>? {
        val transactionType = type ?: TransactionTypes.TypeSend
        val queryMap = mutableMapOf(
            QueryTags.TagsTxType to transactionType.toHex(),
        )
        minterHttpApiOld.getTransactionsRaw(queryMap, 1,1)?.let {
            if (it.isNotEmpty()) {
                assert(true)
//                value4test.hash= it.first().hash
                return it
            }
        } ?: run{
            assert(false)
        }
        return null
    }


//    @Test
    fun getAllMethod(){
        HttpMethod.values().forEach {
            if (it== HttpMethod.STATUS) return getStatus()
        }
        assert(true, { "@TODO" }) // TODO("add method")
//        assert(false, { "ggg" })
    }

}