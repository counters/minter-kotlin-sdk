package counters.minter.sdk.minter.help

import com.google.gson.JsonObject
import counters.minter.sdk.minter.models.TransactionRaw

class Serializer {

    private val serializerTransaction = SerializerTransaction()
    fun transaction(data: TransactionRaw) = serializerTransaction.encode(data)
    fun transaction(jsonObject: JsonObject) = serializerTransaction.decode(jsonObject)
    fun transaction(json: String) = serializerTransaction.decode(json)
}