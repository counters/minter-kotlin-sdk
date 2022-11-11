package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.enums.CandidateStatus
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.Candidate
import counters.minter.sdk.minter.models.Stake
import org.json.JSONObject

class ParseNode: MinterMatch() {
    var minterMatch = MinterMatch()
    @Deprecated("")
    fun get(
        result: JSONObject,
        reward_address: ((address: String) -> Long)? = null,
        owner_address: ((address: String) -> Long)? = null,
        control_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
//        val height = result["height"]
//        val reward = result.getLong("reward_address")
//        val owner = result.getLong("owner_address")
        val _reward = result.getString("reward_address")
        val reward = reward_address?.invoke(_reward)

        val _owner = result.getString("owner_address")
        val owner = owner_address?.invoke(_owner)

        val _control = result.getString("control_address")
        val control = control_address?.invoke(_control)

        if (reward == null || owner == null || control == null) return null
        val pub_key = result.getString("public_key")

        val commission = result.getInt("commission")
        val used_slots = result.getInt("used_slots")
        val uniq_users = result.getInt("uniq_users")
        val min_stake = minterMatch.getAmount(result.getString("min_stake"))

        val crblock = if (result.isNull("crblock")) 1 else result.getLong("crblock")
//      val proposer = if (result.isNull("proposer")) "0" else result.getString("proposer")
//        val node: counter.sdk.Minter.Node? = null
        val node =
            Minter.Node(null, reward, owner, control, pub_key, commission, used_slots, uniq_users, min_stake, crblock)
//        println (node)
        return node
//        return Block.Block(height,)
    }

    fun getCandidate(result: JSONObject): Candidate? {

        val reward = result.getString("reward_address")

        val owner = result.getString("owner_address")

        val control = result.getString("control_address")

        if (reward == null || owner == null || control == null) return null
        val pub_key = result.getString("public_key")
        val total_stake = getAmount(result.getString("total_stake"))

        val commission = result.getInt("commission")
        val used_slots = result.getInt("used_slots")
        val uniq_users = result.getInt("uniq_users")
        val min_stake = getAmount(result.getString("min_stake"))

        val validator = result.getBoolean("validator")
        val jailedUntil = result.getLong("jailed_until")
        val status = CandidateStatus.byRaw(result.getInt("status"))

        val stakes = arrayListOf<Stake>()
        result.getJSONArray("stakes").forEach { stake->
            stake as JSONObject
            CoinObjClass.fromJson(stake.getJSONObject("coin"))?.let { coin ->

            stakes.add( Stake(
                owner = stake.getString("owner"),
                coin = coin,
                value = getAmount(stake.getString("value")),
                bipValue = getAmount(stake.getString("bip_value")),
            ))
        }
        }
        if (status!=null)
        return Candidate(reward, owner, control,total_stake, pub_key, commission, used_slots, uniq_users, min_stake, stakes, status, validator, jailedUntil)
        return null
    }
}
