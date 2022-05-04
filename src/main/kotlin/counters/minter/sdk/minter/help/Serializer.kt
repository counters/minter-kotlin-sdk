package counters.minter.sdk.minter.help

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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

    fun event(jsonArray: JsonArray) = serializerEvent.decode(jsonArray)
    fun event(jsonObject: JsonObject) = serializerEvent.decode(jsonObject)

    fun event(json: String) = serializerEvent.decode(json)
    fun events(json: String) = serializerEvent.decode(JsonParser.parseString(json).asJsonArray)

}