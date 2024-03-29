package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.BlockResponse
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.models.TransactionRaw
import org.joda.time.DateTime

class ConvertBlock {

    private var minterMatch = MinterMatch()
    private val convertTransaction = ConvertTransaction()

    var exception: Boolean = true
        set(value) {
            field = value
            convertTransaction.exception = value
        }

    init {
        convertTransaction.exception = exception
    }

    fun get(response: BlockResponse): MinterRaw.BlockRaw {

        val transaction = arrayListOf<TransactionRaw>()
        val validators = arrayListOf<MinterRaw.SignedValidatorsRaw>()

        val datetime = DateTime(response.time)

        response.transactionsList.forEach {
            transaction.add(convertTransaction.get(it))
        }
        response.validatorsList.forEach {
            validators.add(MinterRaw.SignedValidatorsRaw(it.publicKey, it.signed))
        }
        val blockReward = if (response.hasBlockReward()) minterMatch.getAmount(response.blockReward.value) else null

        return MinterRaw.BlockRaw(
            height = response.height,
            time = datetime,
            num_txs = response.transactionCount.toInt(),
//            total_txs = -1,
            reward = blockReward,
            size = response.size,
            proposer = response.proposer,
            transaction = transaction,
            validators = validators,
        )
    }

}

