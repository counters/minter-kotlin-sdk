package MinterApi

import Minter.*
import org.json.JSONObject

class ParseTransaction {
    var minterMatch = MinterMatch()
    //    defaultCoin
    fun getRaw(result: JSONObject, height: Long): MinterRaw.TransactionRaw? {
        var gascoin: String = ""
        var coin: String? = null
        var node: String? = null
        var from: String = ""
        var to: String? = null

        val transaction = get(result, height, {
            coin = it
            0 // Coin
        }, {
            gascoin = it
            0 //getGasCoin
        }, {
            from = it
            0L // getFromWallet
        }, {
            to = it
            0 //getToWallet
        }, {
            node = it
            0 // Node
        }, fun(_: JSONObject, address: String): Int? {
//            coin = address
            return 0 // CreateCoin
        }, fun(jsonObject: JSONObject, type: Int) {
            // Other
        }
        )

        if (transaction != null) {
            val transactionRaw = MinterRaw.TransactionRaw(
                transaction.hash,
                transaction.height,
                transaction.type,
                from,
                to,
                node,
                transaction.stake,
                coin,
                transaction.amount,
                transaction.gas_price,
                transaction.commission,
                transaction.payload,
                transaction.gas,
                gascoin
            )
            return transactionRaw
        }
        return null
    }

    fun get(
        result: JSONObject, height: Long,
        getCoin: ((symbol: String) -> Int),
        getGasCoin: ((symbol: String) -> Int),
        getFromWallet: ((address: String) -> Long),
        getToWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
        getCreateCoin: ((jsonObject: JSONObject, address: String) -> Int?)? = null,
        getOther: ((jsonObject: JSONObject, type: Int) -> Unit)
    ): Minter.Transaction? {
//        println(result)
        var transaction: Minter.Transaction? = null
        if (result.isNull("code")) {

            val type = result.getInt("type")
            val gas_price = result.getInt("gas_price")
            val gas = result.getInt("gas")
            val gas_coin_str = result.getString("gas_coin")

//            val gas_coin = this.getCoin(gas_coin_str, getGasCoin)
            val gas_coin = getGasCoin(gas_coin_str)

//        val from_str = result.getString("from")
            val _payload = result.getString("payload")
            val hash = result.getString("hash")
            var payload = false
            if (_payload != "") {
                payload = true
            }

            val fromStr = result.getString("from")
            val from = getFromWallet(fromStr)

            var to: Long? = null
            var stake: String? = null
            var coin: Int? = null
            var node: Int? = null
            var amount: Double? = null

            var commission: Int? = null

            if (type == TransactionTypes.TypeMultiSend) {
//                val data_list = result.getJSONObject("data").getJSONArray("list")
//            println("TransactionTypes.TypeMultiSend $data_list")
            } else {

                val data = result.getJSONObject("data")
//            println("TransactionTypes $type $data")

                if (type == TransactionTypes.TypeSend) {
                    to = getToWallet(data.getString("to"))
                } else if (type == TransactionTypes.TypeDelegate || type == TransactionTypes.TypeUnbond) {
                    stake = data.getString("value")
                    amount = minterMatch.getAmount(stake)
                    node = getNode(data.getString("pub_key"))
                } else if (type == TransactionTypes.TypeSetCandidateOnline) {
                    node = getNode(data.getString("pub_key"))
                } else if (type == TransactionTypes.TypeDeclareCandidacy) {
                    node = getNode(data.getString("pub_key"))
                    stake = data.getString("stake")
                    amount = minterMatch.getAmount(stake)
                    commission = data.getInt("commission")
                } else if (type == TransactionTypes.TypeCreateCoin) {
//                node = getNode(data.getString("pub_key"))
                    stake = data.getString("initial_amount")
                    amount = minterMatch.getAmount(stake)
//                commission=data.getInt("commission")

//                coin = this.getCoin(data.getString("symbol"), getCoin)
                    getCoin(data.getString("symbol"))
                    coin = getCreateCoin?.invoke(data, fromStr)

                } else if (type == TransactionTypes.TypeSellAllCoin) {
                    coin = this.getCoin(data.getString("coin_to_sell"), getCoin)
                    stake = result.getJSONObject("tags").getString("tx.sell_amount")
                    amount = minterMatch.getAmount(stake)
                    /*tags	tx.sell_amount*/
                } else if (type == TransactionTypes.TypeBuyCoin) {
                    coin = this.getCoin(data.getString("coin_to_sell"), getCoin)
                    stake = result.getJSONObject("tags").getString("tx.return")
                    amount = minterMatch.getAmount(stake)
                    /*tags	tx.sell_amount*/
                } else if (type == TransactionTypes.TypeSellCoin) {
                    coin = this.getCoin(data.getString("coin_to_sell"), getCoin)
                    stake = result.getJSONObject("tags").getString("tx.return")
                    amount = minterMatch.getAmount(stake)
                    /*tags	tx.sell_amount*/
                } else if (type == TransactionTypes.TypeEditCandidate) {
                    node = getNode(data.getString("pub_key"))
                    to = getToWallet(data.getString("reward_address"))
                }


//            to = getWallet(data.getString("address"))
                if (coin==null && type != TransactionTypes.TypeCreateCoin&& type != TransactionTypes.TypeEditCandidate  && type != TransactionTypes.TypeSetCandidateOnline  && type != TransactionTypes.TypeSetCandidateOffline && type != TransactionTypes.TypeRedeemCheck  && type != TransactionTypes.TypeCreateMultisig )
                {
                    if (data.getString("coin") == Conf.defaultCoin) getCoin(data.getString("coin"))
                    coin = this.getCoin(data.getString("coin"), getCoin)
                }


            }
            getOther.invoke(result, type)
//        println("type $type gas_price $gas_price gas $gas gas_coin $gas_coin from $from        ")
            transaction = Minter.Transaction(
                null,
                hash,
                height,
                type,
                from,
                to,
                node,
                stake,
                coin,
                amount,
                gas_price,
                commission,
                payload,
                gas,
                gas_coin
            )
        }
        return transaction
    }

    fun getCoin(symbol: String, getCoin: ((symbol: String) -> Int)): Int {
        if (symbol != Conf.defaultCoin)
            return getCoin(symbol)
        return Conf.defaultCoinUid
    }
}