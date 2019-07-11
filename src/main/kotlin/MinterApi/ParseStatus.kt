package MinterApi

import Minter.Minter
import org.joda.time.DateTime
import org.json.JSONObject

class ParseStatus {

    fun get(result: JSONObject): Minter.Status? {
//        println(result)
        var status: Minter.Status? = null
        if (result.isNull("code")) {
            val tm_status = result.getJSONObject("tm_status")
            val network = tm_status.getJSONObject("node_info").getString("network")
            val sync_info = tm_status.getJSONObject("sync_info")
            val latest_block_height = sync_info.getLong("latest_block_height")
            val latest_block_time = sync_info.getString("latest_block_time")
            val datetime = DateTime(latest_block_time)

            status = Minter.Status(
                latest_block_height,
                datetime,
                network
            )
        }
        return status
    }

/*
    jsonrpc	"2.0"
    id	""
    result
        version	"1.0.2"
        latest_block_hash	"89C3FE7E96F51D33F0822EC971AD028AE9F4652FBEE3E16A92274985686F5117"
        latest_app_hash	"81C2F6C6DB5ED7787C5309CCA61D1E751C55A21864CC01C5E7E079289400EFA8"
        latest_block_height	"300397"
        latest_block_time	"2019-06-02T09:51:53.836290621Z"
        state_history	"on"
        tm_status
            node_info
                protocol_version
                p2p	"7"
                block	"10"
                app	"5"
                id	"5867a7b60490effb4be27753fa21cd16b5389d71"
                listen_addr	"tcp://0.0.0.0:26656"
                network	"minter-mainnet-1"
                version	"0.31.5"
                channels	"4020212223303800"
                moniker	"minternode"
                other
                tx_index	"on"
                rpc_address	"tcp://0.0.0.0:26657"
            sync_info
                latest_block_hash	"89C3FE7E96F51D33F0822EC971AD028AE9F4652FBEE3E16A92274985686F5117"
                latest_app_hash	"81C2F6C6DB5ED7787C5309CCA61D1E751C55A21864CC01C5E7E079289400EFA8"
                latest_block_height	"300397"
                latest_block_time	"2019-06-02T09:51:53.836290621Z"
                catching_up	false
            validator_info
                address	"C6414F58659115A631529070F1939CB95A71D21F"
                pub_key
                type	"tendermint/PubKeyEd25519"
                value	"NYoP4b0A5+1YFVKHwG8FyZHkMs5d1TiiUbjOo+F/2Ws="
                voting_power	"0"
    */
}