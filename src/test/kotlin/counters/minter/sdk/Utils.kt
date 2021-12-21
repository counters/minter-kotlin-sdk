package counters.minter.sdk

import com.google.common.io.Resources.getResource
import org.json.JSONArray
import kotlin.random.Random
import kotlin.random.nextUInt

class Utils(val network: Network) {

    enum class Network(val str: String) {
        Mainnet4("minter-mainnet-4"),
    }

    enum class SUBJECT(val str: String) {
        ExtremeDelegators("ExtremeDelegators.js"),
        NumismatistsAddresses("NumismatistsAddresses.js"),
        TransactionsBlocks("TransactionsBlocks.js"),
    }

    fun subject(subject: SUBJECT, num: Int? = null, random: Boolean = false): List<String> {
        val json = getJSONArray(subject)
        val array = arrayListOf<String>()
        val _num = if (num!=null) { if(num>0) num else 1 } else { json.count() }
        if (random) {
            val until = json.count()-1
            repeat(_num) {
                Random.nextUInt(until.toUInt()).let {
                    array.add(json[it.toInt()] as String)
                }
            }
        } else {
            json.forEach {
                if (array.count() >= _num) return@forEach
                array.add(it as String)
            }
        }
        return array
    }


    private fun getJSONArray(subject: SUBJECT) = getJSONArray("subjects/${network.str}/${subject.str}")

    private fun getJSONArray(filepatch: String): JSONArray {
        getResource(filepatch).readText().let {
            filter(it, "[" to "]").let {
                return JSONArray(it)
            }
        }
    }

    private fun filter(text: String, quotes: Pair<String, String>): String {
        return text.substring(text.indexOf(quotes.first), text.lastIndexOf(quotes.second) + 1)
    }

}