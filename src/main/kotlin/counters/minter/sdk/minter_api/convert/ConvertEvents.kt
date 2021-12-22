package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.EventsResponse
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.enum.CommissionKey
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.models.Commission
import counters.minter.sdk.minter.utils.EventRole
import counters.minter.sdk.minter.utils.EventType
import mu.KotlinLogging

class ConvertEvents : MinterMatch() {

    private val eventType = EventType
    private val eventRole = EventRole

    private val logger = KotlinLogging.logger {}

    fun get(response: EventsResponse, height: Long): List<MinterRaw.EventRaw> {
        val array = arrayListOf<MinterRaw.EventRaw>()
//        val array = ArrayList<MinterRaw.EventRaw>()

//        var role: String? = null
        response.eventsList.forEach {

            var node: String? = null
            var wallet: String? = null
            var coin: CoinObjClass.CoinObj? = null
            var coinId: Long? = null
//            var type: String? = null
//        var amount: Double? = null
            var rawRole: String? = null
            var pipAmount: String? = null
            var option: Any? = null


            val strType = it.getFieldsOrThrow("type").stringValue
            val structValue = it.getFieldsOrThrow("value").structValue
            val type = eventType.get(strType)
//            println("type $strType[$type]")
            if (type == EventType.Reward) {
                wallet = structValue.getFieldsOrThrow("address").stringValue
                pipAmount = structValue.getFieldsOrThrow("amount").stringValue
//                amount = getAmount(pipAmount)

                rawRole = structValue.getFieldsOrThrow("role").stringValue
                node = structValue.getFieldsOrThrow("validator_pub_key").stringValue
                coinId = structValue.getFieldsOrThrow("for_coin").stringValue.toLong()
//                coin = CoinObjClass.CoinObj(coinId, null)
            } else if (type == EventType.Unbond) {
                wallet = structValue.getFieldsOrThrow("address").stringValue
                pipAmount = structValue.getFieldsOrThrow("amount").stringValue
//                amount = getAmount(pipAmount)
                coinId = structValue.getFieldsOrThrow("coin").stringValue.toLong()
//                coin = CoinObjClass.CoinObj(coinId, null)
                node = structValue.getFieldsOrThrow("validator_pub_key").stringValue
            } else if (type == EventType.UpdateNetwork) {
                val version = structValue.getFieldsOrThrow("version").stringValue
                println("version: $version")
//                println(it)
            } else if (type == EventType.StakeKick) {
                node = structValue.getFieldsOrThrow("validator_pub_key").stringValue
                wallet = structValue.getFieldsOrThrow("address").stringValue
                pipAmount = structValue.getFieldsOrThrow("amount").stringValue
                coinId = structValue.getFieldsOrThrow("coin").stringValue.toLong()
//                coin = CoinObjClass.CoinObj(coinId, null)
//                println(it)
            } else if (type == EventType.UpdateCommissions) {
                val array = arrayListOf<Commission>()
                structValue.fieldsMap.forEach { key, value ->
                    val amount = getAmount(value.stringValue)
                    CommissionKey.fromStr(key)?.let {
                        array.add(Commission(it, amount))
                    } ?: run {
                        val message = "Error: ${CommissionKey.fromStr(key)}"
                        logger.error { message }
                        throw Exception(message)
                    }
                }
                option = listOf(array)
            }  else if (type == EventType.RemoveCandidate) {
                node = structValue.getFieldsOrThrow("candidate_pub_key").stringValue
            } else {
                println(it)
                TODO()
            }

            val amount = pipAmount?.let { getAmount(it) } ?: run { null }
            coin = coinId?.let { CoinObjClass.CoinObj(it, null) }

            val role = if (rawRole != null) eventRole.get(rawRole).name else null

            val eventRaw = MinterRaw.EventRaw(
                height = height,
                node = node,
                wallet = wallet,
                coin = coin,
                type = type.name,
                amount = amount,
                option = option,
                role = role
            )
            array.add(eventRaw)
        }
        return array
    }
}