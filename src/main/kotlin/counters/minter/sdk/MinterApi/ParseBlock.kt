package counters.minter.sdk.MinterApi

import counters.minter.sdk.Minter.Minter
import counters.minter.sdk.Minter.MinterMatch
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONObject

class ParseBlock {
    val minterMatch = MinterMatch()

    enum class Method {
        BLOCK,
        NODE,
    }

    fun get(
        result: JSONObject,
        proposer_call: ((pub_key: String) -> Int?)? = null,
        transactions: ((transactions: JSONArray) -> Unit)? = null,
        validators: ((validators: JSONArray) -> Unit)? = null
    ): Minter.Block {
        val height = result.getLong("height")

        val _proposer = if (result.isNull("proposer")) null else result.getString("proposer")

        var proposer: Int? = null
        if (_proposer != null) {
            proposer = proposer_call?.invoke(_proposer) //pub_key
        }
        if (!result.isNull("validators")) {
            validators?.invoke(result.getJSONArray("validators")) //pub_key
        }

        val time = DateTime(result.getString("time"))

        transactions?.invoke(result.getJSONArray("transactions"))

        val num_txs = if (result.isNull("transaction_count")) 0 else result.getInt("transaction_count")

        val total_txs = if (result.isNull("total_txs")) 0 else result.getInt("total_txs")
        val size = result.getLong("size")
        val block_reward = result.getString("block_reward")
        val reward = minterMatch.getAmount(block_reward)

//        val transaction = ArrayList<counter.sdk.Minter.Transaction>()
//        val signedValidators = ArrayList<counter.sdk.Minter.SignedValidators>()
        val block = Minter.Block(height, time, num_txs, total_txs, reward, size, proposer/*, transaction, validators*/)
//        println (block)
        return block
    }
}
