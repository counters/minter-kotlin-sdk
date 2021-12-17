package counters.minter.sdk.minter_api

import counters.minter.grpc.client.BlockResponse
import counters.minter.grpc.client.StatusResponse
import counters.minter.grpc.client.TransactionResponse
import counters.minter.sdk.lib.LibTransactionTypes
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import mu.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.Semaphore

internal class MinterGrpcApiTest {

    private val grpcOptions = Config.grpcOptions

    private val logger = KotlinLogging.logger {}

    data class Value4test(
        var init: Boolean = false,
        var status: Minter.Status? = null,
        var block: Long? = null,
        var address: String? = null,
        var hash: String? = null,
//        var status: Minter.Status? = null,
    )

    private val value4test = Value4test(
        block = 3996983,
        hash = "Mt033ee4863bd03b346a2bc67b5184ee60baf5780cefdab748368d323d5e8c5b97",
        address = "Mx0903ab168597a7c86ad0d4b72424b3632be0af1b",
    )

    @Test
    fun getStatus() {
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        assertNotEquals(null, minterGrpcApi.getStatus())
    }

    @Test
    fun getStatusGrpc() {
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        assertNotEquals(null, minterGrpcApi.getStatusGrpc())
    }

/*    @Test
    fun asyncStatusGrpc_Old() {
    }*/

    @Test
    fun asyncStatusGrpc() {

        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        var statusResponse: StatusResponse? = null
        val semaphore = Semaphore(0)
        minterGrpcApi.asyncStatusGrpc {
            statusResponse = it
            assertNotEquals(null, statusResponse)
            semaphore.release()
        }
        semaphore.acquire()
        assertNotEquals(null, statusResponse)
    }

    @Test
    fun asyncBlockGrpc() {
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        var asyncBlock: BlockResponse? = null
        val semaphore = Semaphore(0)
        minterGrpcApi.asyncBlockGrpc(value4test.block!!) {
//            logger.info { it }
            asyncBlock = it
            assertEquals(value4test.block, asyncBlock?.height)
            semaphore.release()
        }
        semaphore.acquire()
        assertNotEquals(null, asyncBlock?.height)
    }

    @Test
    fun blockGrpc() {
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        assertEquals(value4test.block!!, minterGrpcApi.blockGrpc(value4test.block!!)?.height)
    }

    @Test
    fun transaction() {
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
//        logger.info { "!!!!!!${minterApi2.transaction(value4test.hash!!)}" }
        assertEquals(value4test.hash!!, minterGrpcApi.transaction(value4test.hash!!)?.hash)
    }

    @Test
    fun transactionGrpc() {
        var transactionResponse: TransactionResponse? = null
        val semaphore = Semaphore(0)
        val minterGrpcApi = MinterGrpcApi(grpcOptions)
        minterGrpcApi.transactionGrpc(value4test.hash!!) {
//            logger.info { it }
            transactionResponse = it
            semaphore.release()
            assertEquals(value4test.hash, transactionResponse?.hash)
        }
        semaphore.acquire()
//        logger.info { "transactionResponse $transactionResponse" }
        assertNotEquals(null, transactionResponse)

//        LibTransactionTypes.mapTypeTrs[TransactionTypes.TypeSend]
    }

}