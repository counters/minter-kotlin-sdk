package counters.minter.sdk

import com.google.common.io.Resources.getResource
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter.utils.EventType
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random
import kotlin.random.nextUInt

class Utils(val network: Network) {

    enum class Network(val str: String) {
        Mainnet4("minter-mainnet-4"),
        Mainnet5("minter-mainnet-5"),
        Taconet13("v2.6.0-testnet13"),
        TestNet("minter3-testnet1"),

    }

    enum class SUBJECT(val str: String) {
        ExtremeDelegators("ExtremeDelegators.js"),
        NumismatistsAddresses("NumismatistsAddresses.js"),
        TransactionsBlocks("TransactionsBlocks.js"),
        FailedTransactionsBlocks("FailedTransactionsBlocks.js"),
        Events("Events.js"),
    }

    fun getFailedTransactions(type: TransactionTypes? = null, num: Int? = null, random: Boolean = false): List<String> {
        getJSONArray(SUBJECT.FailedTransactionsBlocks).let {
            getTransactionsBlocks(it, "hash", type).let {
                return prepareJson(it, num, random)
            }
        }
    }

    fun getTransactions(type: TransactionTypes? = null, num: Int? = null, random: Boolean = false): List<String> {
        getJSONArray(SUBJECT.TransactionsBlocks).let {
            getTransactionsBlocks(it, "hash", type).let {
                return prepareJson(it, num, random)
            }
        }
    }

    fun getBlock(type: TransactionTypes? = null, num: Int? = null, random: Boolean = false) = subject(SUBJECT.TransactionsBlocks, num, random, "height", type)
    fun getFailedBlock(type: TransactionTypes? = null, num: Int? = null, random: Boolean = false) = subject(SUBJECT.FailedTransactionsBlocks, num, random, "height", type)

    fun getExtremeDelegators(num: Int? = null, random: Boolean = false) = subject(SUBJECT.ExtremeDelegators, num, random)
    fun getNumismatistsAddresses(num: Int? = null, random: Boolean = false) = subject(SUBJECT.NumismatistsAddresses, num, random)

    fun getEvents(eventType: EventType.Data? = null, num: Int? = null, random: Boolean = false) = subject(subject = SUBJECT.Events, num = num, random = random, eventType = eventType)


    private fun subject(subject: SUBJECT, num: Int? = null, random: Boolean = false, tag: String? = "hash", type: TransactionTypes? = null, eventType: EventType.Data? = null): List<String> {
        val json = getJSONArray(subject)
        val jsonArray = if (subject == SUBJECT.ExtremeDelegators || subject == SUBJECT.NumismatistsAddresses) json
        else if ((subject == SUBJECT.TransactionsBlocks || subject == SUBJECT.FailedTransactionsBlocks) && tag!=null && type!=null ) getTransactionsBlocks(json, tag, type)
        else if ((subject == SUBJECT.Events ) ) getEventsJson(json, eventType)
        else TODO()
        return prepareJson(jsonArray, num, random)
    }


    private fun prepareJson(jsonArray: JSONArray, num: Int? = null, random: Boolean = false): List<String> {
        val array = arrayListOf<String>()
        val _num = if (num != null) {
            if (num > 0) num else 1
        } else {
            jsonArray.count()
        }
        val until = jsonArray.count() - 1
        if (random && until>0) {
//            if (until<0) return array
            repeat(_num) {
                Random.nextInt(0, until).let {
                    array.add(jsonArray[it] as String)
                }
            }
        } else {
            jsonArray.forEach {
                if (array.count() >= _num) return@forEach
                array.add(it as String)
            }
        }
        return array
    }

    private fun randomType(): TransactionTypes {
        val list = TransactionTypes.values()
        val random = Random.nextInt(0, (list.count() - 1))
        return list[random]
    }

    private fun randomTypeEvent(): EventType.Data {
//        val list = TransactionTypes.values()
//        val random = Random.nextInt(0, (list.count() - 1))
        return EventType.Reward
    }

    private fun getTransactionsBlocks(jsonArray: JSONArray, tag: String, type: TransactionTypes? = null): JSONArray {
        val _type = type ?: randomType()
        jsonArray.forEach {
            it as JSONObject
            if (it.getInt("type") == _type.int) {
                return filterJSONArray(it.getJSONArray("list"), tag)
            }
        }
        return JSONArray()
    }

    private fun getEventsJson(jsonArray: JSONArray, eventType: EventType.Data? =null): JSONArray {
        val _type = eventType ?: randomTypeEvent()
        jsonArray.forEach {
            it as JSONObject
            if (it.getString("type") == _type.raw) {
                return it.getJSONArray("height")
            }
        }
        return JSONArray()
    }


    private fun filterJSONArray(jsonArray: JSONArray, type: String): JSONArray {
        val newJson = JSONArray()
        jsonArray.forEach {
            newJson.put((it as JSONObject).getString(type))
        }
        return newJson
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