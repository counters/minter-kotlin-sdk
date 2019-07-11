package MinterApi

import Minter.Minter
import Minter.MinterMatch
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
        transactions: ((transactions: JSONArray) -> Unit)? = null
    ): Minter.Block {
//        val height = result["height"]
        val height = result.getLong("height")

//        val proposer2: String= result.getString("proposer")
        val _proposer = if (result.isNull("proposer")) null else result.getString("proposer")

        var proposer: Int? = null
        if (_proposer != null) {
            proposer = proposer_call?.invoke(_proposer) //pub_key
        }
        /*  val proposer = try {
              result.getString("proposer")
          } catch (e: Exception) {
              "0"
  //            println(e)
          }*/
//        result.getInt()
//        proposer  = result["proposer"] as String
//        var time = LocalDateTime.parse(result.getString("time"))
//        val time = ZonedDateTime.parse(result.getString("time"))
        val time = DateTime(result.getString("time"))

//        println(DateTime(result.getString("time")))
//        val date = java.text.SimpleDateFormat("yyyy.MM.dd' 'HH:mm:ss'Z'", java.util.Locale.getDefault()).format(java.util.Date(mtime * 1000));//
//

        transactions?.invoke(result.getJSONArray("transactions"))

        val num_txs = result.getInt("num_txs")
        val total_txs = result.getInt("total_txs")
        val size = result.getLong("size")
        val block_reward = result.getString("block_reward")
        val reward = minterMatch.getAmount(block_reward)

//        val sss = BigDecimal(block_reward)*BigDecimal(0.000000000000000001)
//        println(sss)
//        new BigDecimal(decimalString).setScale(18, BigDecimal.ROUND_UNNECESSARY)
//        println("height:$height,proposer:$proposer,num_txs:$num_txs,total_txs:$total_txs,size:$size,time:$time,")
        val block = Minter.Block(height, time, num_txs, total_txs, /*block_reward,*/ reward, size, proposer)
//        println (block)
        return block
//        return Block.Block(height,)
    }
}
