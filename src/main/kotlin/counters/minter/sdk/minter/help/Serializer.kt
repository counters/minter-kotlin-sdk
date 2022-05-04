package counters.minter.sdk.minter.help

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import counters.minter.sdk.minter.MinterRaw.EventRaw
import counters.minter.sdk.minter.models.TransactionRaw

class Serializer {

    private val serializerTransaction = SerializerTransaction()
    private val serializerEvent = SerializerEvent()

    fun transaction(transactionRaw: TransactionRaw) = serializerTransaction.encode(transactionRaw)
    fun transaction(jsonObject: JsonObject) = serializerTransaction.decode(jsonObject)
    fun transaction(json: String) = serializerTransaction.decode(json)

    fun event(eventRaw: EventRaw) = serializerEvent.encode(eventRaw)
    fun event(eventsRaw: List<EventRaw>) = serializerEvent.encode(eventsRaw)

/*    fun event(json: JSONObject): EventRaw? {
        TODO("Not yet implemented")
    }*/

    fun event(jsonArray: JsonArray) = serializerEvent.decode(jsonArray)


    fun event(jsonObject: JsonObject) = serializerEvent.decode(jsonObject)


/*    fun events(height: List<MinterRaw.EventRaw>): JsonObject? {
        TODO("Not yet implemented")
    }*/
}