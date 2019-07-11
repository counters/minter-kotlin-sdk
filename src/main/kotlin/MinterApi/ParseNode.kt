package MinterApi

import Minter.Minter
import org.json.JSONObject

class ParseNode {

    fun get(
        result: JSONObject,
        reward_address: ((address: String) -> Long)? = null,
        owner_address: ((address: String) -> Long)? = null
    ): Minter.Node? {
//        val height = result["height"]
//        val reward = result.getLong("reward_address")
//        val owner = result.getLong("owner_address")
        val _reward = result.getString("reward_address")
        val reward = reward_address?.invoke(_reward)

        val _owner = result.getString("owner_address")
        val owner = owner_address?.invoke(_owner)
        if (reward == null || owner == null) return null
        val pub_key = result.getString("pub_key")
        val commission = result.getInt("commission")
        val crblock = result.getLong("created_at_block")
//      val proposer = if (result.isNull("proposer")) "0" else result.getString("proposer")
//        val node: Minter.Node? = null
        val node = Minter.Node(null, reward, owner, pub_key, commission, crblock)
//        println (node)
        return node
//        return Block.Block(height,)
    }
}