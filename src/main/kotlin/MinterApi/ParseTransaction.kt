package MinterApi

import Minter.*
import org.json.JSONObject
import Minter.CoinObjClass.CoinObj

class ParseTransaction {
    var minterMatch = MinterMatch()
    //    defaultCoin
    fun getRaw(result: JSONObject, height: Long): MinterRaw.TransactionRaw? {
        var gascoin: CoinObj = CoinObj(0, "")
        var coin: CoinObj? = null
        var coin2: CoinObj? = null
        var node: String? = null
        var from: String = ""
        var to: String? = null
        var optDouble: Double?=null
        var optString: String?=null
        var optList: ArrayList<Any>?=null
        var payloadByte: String?=null

        val transaction = get(result, height,
            { idCoin, symbolCoin ->
                val coinId = if (idCoin == null) -1L else idCoin
                coin = CoinObj(coinId, symbolCoin)
                coin!! // Coin
            }, { idCoin, symbolCoin ->
                coin2 = CoinObj(idCoin, symbolCoin)
                coin2!! // Coin
            }, { idCoin, symbolCoin ->
//            gascoin = it
                gascoin = CoinObj(idCoin, symbolCoin)
                gascoin //getGasCoin
            }, {
                from = it
                0L // getFromWallet
            }, {
                to = it
                0 //getToWallet
            }, {
                node = it
                0 // Node
            }, fun(_: JSONObject, _: JSONObject, address: String): CoinObj? {
//            coin = address
                return CoinObj(0, "") // CreateCoin
            }, {
                if (optList == null) optList = arrayListOf()
                val multisendItem = MinterRaw.MultisendItemRaw(it.address, it.value, it.coin)
                optList!!.add(multisendItem)
                multisendItem // Node
            },
            fun(jsonObject: JSONObject, type: Int) {
                if ( !jsonObject.isNull("payload") ) payloadByte = jsonObject.getString("payload")
                if (payloadByte=="") payloadByte =null
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
                transaction.coin,
                transaction.coin2,
                transaction.amount,
                transaction.gas_price,
                transaction.commission,
                transaction.payload,
                transaction.gas,
                gascoin,
                transaction.optDouble,
                transaction.optString,
                optList,
                payloadByte
            )
            return transactionRaw
        }
        return null
    }

    fun get(
        result: JSONObject, height: Long,
        getCoin: ((gas_coin_id: Long?, symbol: String?) -> CoinObj),
        getCoin2: ((gas_coin_id: Long, symbol: String?) -> CoinObj),
        getGasCoin: ((gas_coin_id: Long, symbol: String?) -> CoinObj),
        getFromWallet: ((address: String) -> Long),
        getToWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
        getCreateCoin: ((jsonObject: JSONObject, tagsObject: JSONObject, address: String) -> CoinObj?)? = null,
        getMultisendItem: ((multisendItemRaw: MinterRaw.MultisendItemRaw) -> MinterRaw.MultisendItemRaw)? = null,
        getOther: ((jsonObject: JSONObject, type: Int) -> Unit)
    ): Minter.Transaction? {
        var transaction: Minter.Transaction? = null
        if (!result.isNull("code")) {
            if (result.getString("code")=="0") {

                val type = result.getInt("type")
                val gas_price = result.getInt("gas_price")
                val gas = result.getInt("gas")
                val gasCoinObj = result.getJSONObject("gas_coin");
                val gas_coin_str = gasCoinObj.getString("symbol")
                val gas_coin_id = gasCoinObj.getString("id").toLong()

                val gas_coin = getGasCoin(gas_coin_id, gas_coin_str)

                val payload = if ( !result.isNull("payload") ) {
                    if (result.getString("payload")=="") false else true
                } else {
                    false
                }
                val hash = result.getString("hash")

                val fromStr = result.getString("from")
//            val from: Long
                val from = if (type == TransactionTypes.TypeRedeemCheck) {
                    val fromStrRedeemCheck = "Mx" + result.getJSONObject("tags").getString("tx.from")
                    getFromWallet(fromStrRedeemCheck)
                } else {
                    getFromWallet(fromStr)
                }

                var to: Long? = null
                var stake: String? = null
                var coin: CoinObj? = null
                var coin2: CoinObj? = null
                var node: Int? = null
                var amount: Double? = null

                var commission: Int? = null

                var optDouble: Double?=null
                var optString: String?=null


                val tags = if (result.isNull("tags")) null else result.getJSONObject("tags")

                if (type == TransactionTypes.TypeMultiSend) {
                    val data_list = result.getJSONObject("data").getJSONArray("list")
//                println("TransactionTypes.TypeMultiSend $data_list")
                    var globalAmountInMultisend: Double? = 0.0;
                    var globalCoinInMultisend: Long? = null
                    var globalCoinInMultisendSymbol: String? = null
                    var return_data_list = false // @TODO remove patch
//                    optList= arrayListOf(mapOf())

                    data_list.forEach data_list@{
//                        if (return_data_list) return@data_list
                        val innerJsonObject = it as JSONObject
                        val coinInMultisendStr = innerJsonObject.getJSONObject("coin")
                        val coinInMultisend = coinInMultisendStr.getString("id").toLong()
                        val coinInMultisendSymbol = coinInMultisendStr.getString("symbol")
                        val valueInMultisend = innerJsonObject.getString("value")
                        val currValue = minterMatch.getAmount(valueInMultisend)


                        getMultisendItem?.invoke(
                            MinterRaw.MultisendItemRaw(
                                innerJsonObject.getString("to"),
                                currValue,
                                CoinObj(coinInMultisend, coinInMultisendSymbol)
                            )
                        )

                        if (globalCoinInMultisend == null || globalCoinInMultisend == coinInMultisend) {
                            globalCoinInMultisend = coinInMultisend
                            globalCoinInMultisendSymbol = coinInMultisendSymbol
                            if (globalAmountInMultisend != null) globalAmountInMultisend = globalAmountInMultisend!!.plus(currValue)
                        } else {
                            globalAmountInMultisend = null
                            globalCoinInMultisend = null
//                            return_data_list = true
//                            return@data_list
//                        return@forEach
                        }
                    }

                    if (globalCoinInMultisend != null) {
                        coin = this.getCoin(globalCoinInMultisend!!, globalCoinInMultisendSymbol!!, getCoin)
                        amount = globalAmountInMultisend
                    }
                } else {

                    val data = result.getJSONObject("data")
//            println("TransactionTypes $type $data")

                    if (type == TransactionTypes.TypeSend) {
                        to = getToWallet(data.getString("to"))
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                    } else if (type == TransactionTypes.TypeDelegate || type == TransactionTypes.TypeUnbond) {
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                        node = getNode(data.getString("pub_key"))

                        val _coin = data.getJSONObject("coin")
                        coin = CoinObj(_coin.getString("id").toLong(), _coin.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                    } else if (type == TransactionTypes.TypeSetCandidateOnline || type == TransactionTypes.TypeSetCandidateOffline) {
                        node = getNode(data.getString("pub_key"))
                    } else if (type == TransactionTypes.TypeDeclareCandidacy) {
                        node = getNode(data.getString("pub_key"))
                        stake = data.getString("stake")
                        amount = minterMatch.getAmount(stake)
                        commission = data.getInt("commission")
                    } else if (type == TransactionTypes.TypeCreateCoin) {
                        getCreateCoin?.invoke(data, tags!!, fromStr)
                        stake = data.getString("initial_amount")
                        amount = minterMatch.getAmount(stake)
                        val coinSymbol_tmp = data.getString("symbol")
                        val coinId_tmp = tags!!.getString("tx.coin_id").toLong()
                        getCoin(coinId_tmp, coinSymbol_tmp)
                        coin = CoinObj(coinId_tmp, coinSymbol_tmp)
                        optDouble = minterMatch.getAmount(data.getString("initial_reserve"))

                    }  else if (type == TransactionTypes.TypeRecreateCoin) {
//                        val tags =result.getJSONObject("tags")
                        getCreateCoin?.invoke(data, tags!!, fromStr)
                        stake = data.getString("initial_amount")
                        amount = minterMatch.getAmount(stake)

                        val coinSymbol_tmp = data.getString("symbol")
                        val coinId_tmp = tags!!.getString("tx.coin_id").toLong()
                        getCoin(coinId_tmp, coinSymbol_tmp)
                        coin = CoinObj(coinId_tmp, coinSymbol_tmp)

                        optDouble = minterMatch.getAmount(data.getString("initial_reserve"))

                    } else if (type == TransactionTypes.TypeSellAllCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObj(coin_to_sell.getString("id").toLong(), coin_to_sell.getString("symbol"))
                        coin2 = CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                        this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = tags!!.getString("tx.sell_amount")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags.getString("tx.return"))
                        /*tags	tx.sell_amount*/
                    } else if (type == TransactionTypes.TypeBuyCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        coin2 = CoinObj(coin_to_sell.getString("id").toLong(), coin_to_sell.getString("symbol"))
                        coin = this.getCoin(coin.id, coin.symbol, getCoin)
                        coin2 = this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_buy")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                    } else if (type == TransactionTypes.TypeSellCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObj(coin_to_sell.getString("id").toLong(), coin_to_sell.getString("symbol"))
                        coin2 = CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                        this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_sell")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                    } else if (type == TransactionTypes.TypeEditCandidate) {
                        node = getNode(data.getString("pub_key"))
                        to = getToWallet(data.getString("reward_address"))
                    } else if (type == TransactionTypes.TypeRedeemCheck) {
                        to = getToWallet("Mx" + result.getJSONObject("tags").getString("tx.to"))
//                        coin = this.getCoin(result.getJSONObject("tags").getString("tx.coin"), getCoin) // TODO Найти транзакцию с чеком
                    } else if (type == TransactionTypes.TypeSetHaltBlock) {
                        node = getNode(data.getString("pub_key"))
                    } else if (type == TransactionTypes.TypeEditCoinOwner) { // @TODO Error В TypeEditCoinOwner вообще нет инфы о CoinId, только Symbol
                        to = getToWallet(data.getString("new_owner"))
                        val coinSymbol_tmp = data.getString("symbol")
//                        val tags =result.getJSONObject("tags")
//                        val coinId_tmp = tags.getString("tx.coin_id").toLong()
                        coin = getCoin(null, coinSymbol_tmp)
//                        coin = CoinObj(null, coinSymbol_tmp)
                    } else if (type == TransactionTypes.TypeEditMultisig) {
//                        to = getToWallet(data.getString("new_owner"))
                    } else if (type == TransactionTypes.TypePriceVote) {
                        val price = data.getString("price")
                        optString=price
                        optDouble = (price.toDouble()/1000.0)
                    } else if (type == TransactionTypes.TypeEditCandidatePublicKey) {
                        node = getNode(data.getString("pub_key"))
                        optString = data.getString("new_pub_key")
                    }

                    if (
                        coin == null
                        && type != TransactionTypes.TypeCreateCoin
                        && type != TransactionTypes.TypeEditCandidate
                        && type != TransactionTypes.TypeSetCandidateOnline
                        && type != TransactionTypes.TypeSetCandidateOffline
                        && type != TransactionTypes.TypeRedeemCheck
                        && type != TransactionTypes.TypeCreateMultisig
                        && type < TransactionTypes.TypeEditCandidate
                    ) {
//                        println("height $height hash $hash result $result data $data")
                        val _coin = data.getJSONObject("coin")
                        coin = CoinObj(_coin.getString("id").toLong(), _coin.getString("symbol"))
                        if (coin.symbol == Conf.defaultCoin) {
                            getCoin(coin.id, coin.symbol)
                        } else {
                            this.getCoin(coin.id, coin.symbol, getCoin)
                        }
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
                    coin2,
                    amount,
                    gas_price,
                    commission,
                    payload,
                    gas,
                    gas_coin,
                    optDouble,
                    optString
                )
            }
        }
        return transaction
    }

    fun getCoin(gas_coin_id: Long, symbol: String?, getCoin: ((gas_coin_id: Long, symbol: String?) -> CoinObj)): CoinObj {
//        if (symbol != Conf.defaultCoin)
            return getCoin(gas_coin_id, symbol)
//        return Conf.defaultCoinUid
    }
}