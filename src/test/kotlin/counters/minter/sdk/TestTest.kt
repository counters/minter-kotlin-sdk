package counters.minter.sdk

import counters.minter.sdk.minter.enum.TransactionTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class TestTest {

    private val utils = Utils(Utils.Network.Mainnet4)

    @Test
    fun test() {
        utils.getTransactions( TransactionTypes.TypeSend,5, false).let { println(it) }
        utils.getFailedTransactions( TransactionTypes.TypeSend,5, false).let { println(it) }
        utils.getBlock( TransactionTypes.TypeSend,5, false).let { println(it) }
        utils.getFailedBlock( TransactionTypes.TypeSend,5, false).let { println(it) }
        utils.getNumismatistsAddresses( 5, false).let { println(it) }
        utils.getExtremeDelegators( 5, false).let { println(it) }

        assertNotEquals(null, utils.getTransactions( TransactionTypes.TypeSend, 5, true))
    }
}