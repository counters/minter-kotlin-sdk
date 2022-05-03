package counters.minter.sdk.minter.help

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import counters.minter.sdk.minter.DataMultisig
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.enum.TransactionTypes
import counters.minter.sdk.minter.enum.TxPool
import counters.minter.sdk.minter.models.DataEditCandidate
import counters.minter.sdk.minter.models.TransactionRaw


class SerializerTransaction {

    private val gson = Gson()

    fun encode(transactionRaw: TransactionRaw): JsonObject? {
        return gson.toJsonTree(transactionRaw).asJsonObject
    }

    fun decode(data: String) = decode(JsonParser.parseString(data).asJsonObject)

    fun decode(jsonObject: JsonObject): TransactionRaw? {
//        val data = JSONObject(data)
        val optDataElement = jsonObject["optData"]
        val data = gson.fromJson(jsonObject, TransactionRaw::class.java)
        val type = data.type
        if (TransactionTypes.TypeMultiSend.eq(type)) {
            return data.copy(optData = gson.fromJson(optDataElement, object : TypeToken<List<MinterRaw.MultisendItemRaw>>() {}.type))
//            return data.copy(optData = gson.fromJson<MinterRaw.MultisendItemRaw>(optDataElement/*, MinterRaw.MultisendItemRaw::class.java*/))
        } else if (TransactionTypes.TypeCreateMultisig.eq(type) || TransactionTypes.TypeEditMultisig.eq(type)) {
            return data.copy(optData = gson.fromJson(optDataElement, DataMultisig::class.java))
        } else if (TransactionTypes.SELL_SWAP_POOL.eq(type) || TransactionTypes.SELL_ALL_SWAP_POOL.eq(type) || TransactionTypes.BUY_SWAP_POOL.eq(type)) {
            return data.copy(optData = fromJson<List<TxPool>>(optDataElement))
//            return data.copy(optData = gson.fromJson(optDataElement, object: TypeToken<List<TxPool>>() {}.type))
        } else if (TransactionTypes.TypeEditCandidate.eq(type)) {
            return data.copy(optData = gson.fromJson(optDataElement, DataEditCandidate::class.java))
        } else if (TransactionTypes.MOVE_STAKE.eq(type)) {
            return data.copy(optData = optDataElement.asLong)
        } else if (TransactionTypes.CREATE_SWAP_POOL.eq(type)) {
            return data.copy(optData = gson.fromJson(optDataElement, MinterRaw.PoolRaw::class.java))
        } else if (TransactionTypes.ADD_LIMIT_ORDER.eq(type)) {
            return data.copy(optData = gson.fromJson(optDataElement, LimitOrderRaw::class.java))
        } else if (TransactionTypes.ADD_LIMIT_ORDER.eq(type) || TransactionTypes.REMOVE_LIMIT_ORDER.eq(type)) {
            return data.copy(optData = optDataElement.asLong)
        } else if (TransactionTypes.LOCK_STAKE.eq(type)) {
            return data.copy(optData = optDataElement.asLong)
        } else if (TransactionTypes.LOCK.eq(type)) {
            return data.copy(optData = optDataElement.asLong)
        }

        return data
    }

    //    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)
//    inline fun <reified T> Gson.fromJson(jsonObject: JsonObject) = fromJson<T>(jsonObject, object: TypeToken<T>() {}.type)
//    inline fun <reified T> Gson.fromJson(jsonElement: JsonElement) = fromJson<T>(jsonElement, object: TypeToken<T>() {}.type)
/*    private inline fun <reified T> fromJson(jsonElement: JsonElement): T {
        return gson.fromJson<T>(jsonElement, object : TypeToken<T>() {}.type)
    }*/
    private inline fun <reified T> fromJson(jsonElement: JsonElement) = gson.fromJson<T>(jsonElement, object : TypeToken<T>() {}.type)
}