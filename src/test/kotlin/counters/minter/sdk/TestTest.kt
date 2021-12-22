package counters.minter.sdk

import Config
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter.utils.EventType
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class TestTest {

//    private val utils = Utils(Utils.Network.Mainnet4)
    private val utils = Utils(Config.network)

    @Test
    fun test() {
//        utils.getTransactions( TransactionTypes.TypeSend,5, false).let { println(it) }
//        utils.getFailedTransactions( TransactionTypes.TypeSend,5, false).let { println(it) }
//        utils.getBlock( TransactionTypes.TypeSend,5, false).let { println(it) }
//        utils.getFailedBlock( TransactionTypes.TypeSend,5, false).let { println(it) }
//        utils.getNumismatistsAddresses( 5, false).let { println(it) }
//        utils.getExtremeDelegators( 5, false).let { println(it) }
        utils.getEvents(EventType.Jail, 5, true).let { println(it) }

        assertNotEquals(null, utils.getTransactions( TransactionTypes.TypeSend, 5, true))
    }
}