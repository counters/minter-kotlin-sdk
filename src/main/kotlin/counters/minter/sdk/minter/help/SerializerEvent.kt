package counters.minter.sdk.minter.help

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import counters.minter.sdk.minter.MinterRaw.EventRaw
import counters.minter.sdk.minter.enums.EventTypes
import counters.minter.sdk.minter.models.Commission

class SerializerEvent {
    private val gson = Gson()

    fun encode(eventRaw: EventRaw): JsonObject? {
        return gson.toJsonTree(eventRaw).asJsonObject
    }

    fun encode(eventsRaw: List<EventRaw>): JsonArray? {
        return gson.toJsonTree(eventsRaw).asJsonArray
    }

    fun decode(data: String) = decode(JsonParser.parseString(data).asJsonObject)

    fun decode(jsonObject: JsonObject): EventRaw? {
        val optDataElement = jsonObject["option"]
        val data = gson.fromJson(jsonObject, EventRaw::class.java)
        val type = data.type

        if (EventTypes.UpdateCommissions.eq(type)) {
            return data.copy(option = fromJson<List<Commission>>(optDataElement))
        } else if (EventTypes.StakeMove.eq(type)) {
            return data.copy(option = optDataElement.asString)
        } else if (EventTypes.UpdatedBlockReward.eq(type)) {
            return data.copy(option = optDataElement.asDouble)
        } else if (EventTypes.UpdateNetwork.eq(type)) {
            return data.copy(option = optDataElement.asString)
        } else if (EventTypes.OrderExpired.eq(type)) {
            return data.copy(option = optDataElement.asLong)
        } else if (EventTypes.Jail.eq(type)) {
            return data.copy(option = optDataElement.asLong)
        }/* else if (EventTypes.Unlock.eq(type)) {
            return data.copy(option = optDataElement.asLong)
        }*/
        return data
    }

    fun decode(jsonArray: JsonArray): List<EventRaw>? {
        val events = arrayListOf<EventRaw>()
        jsonArray.forEach {
            decode(it.asJsonObject)?.let {
                events.add(it)
            } ?: run {
                return null
            }
        }
        return events
    }


    private inline fun <reified T> fromJson(jsonElement: JsonElement) = gson.fromJson<T>(jsonElement, object : TypeToken<T>() {}.type)


}
