package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.Enum.CommissionKey
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Models.Commission
import counters.minter.sdk.minter.Models.DataEditCandidate
import counters.minter.sdk.minter.Models.TransactionRaw
import counters.minter.sdk.minter_api.convert.ConvertMultisig
import org.json.JSONObject

class ParseTransaction {
    private var minterMatch = MinterMatch()
    private val parsePoolExchange = ParsePoolExchange()

    //    defaultCoin
    private val convertMultisig = ConvertMultisig


    fun getRaw(result: JSONObject): TransactionRaw? {
        val height = result.getLong("height")
        return getRaw(result, height)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun getRaw(result: JSONObject, height: Long): TransactionRaw? {
        var gascoin: CoinObjClass.CoinObj = CoinObjClass.CoinObj(0, "")
        var coin: CoinObjClass.CoinObj? = null
        var coin2: CoinObjClass.CoinObj? = null
        var node: String? = null
        var from: String = ""
        var to: String? = null
        var optDouble: Double? = null
        var optString: String? = null
//        var optList: ArrayList<Any>?=null
        var optList: Any? = null
        var payloadByte: String? = null


        val transaction = get(result, height,
            { idCoin, symbolCoin ->
                val coinId = idCoin ?: -1L
                coin = CoinObjClass.CoinObj(coinId, symbolCoin)
                coin!! // Coin
            }, { idCoin, symbolCoin ->
                coin2 = CoinObjClass.CoinObj(idCoin, symbolCoin)
                coin2!! // Coin
            }, { idCoin, symbolCoin ->
//            gascoin = it
                gascoin = CoinObjClass.CoinObj(idCoin, symbolCoin)
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
            }, fun(_: JSONObject, _: JSONObject, address: String): CoinObjClass.CoinObj? {
//            coin = address
                return CoinObjClass.CoinObj(0, "") // CreateCoin
            }, {
//                if (optList == null) optList = arrayListOf()
                val multisendItem = MinterRaw.MultisendItemRaw(it.address, it.value, it.coin)
                if (optList == null) optList = arrayListOf<MinterRaw.MultisendItemRaw>()
                (optList as ArrayList<MinterRaw.MultisendItemRaw>).add(multisendItem)
                multisendItem
            },
            fun(jsonObject: JSONObject, type: Int) {
                if (type == TransactionTypes.BUY_SWAP_POOL.int || type == TransactionTypes.SELL_SWAP_POOL.int || type == TransactionTypes.SELL_ALL_SWAP_POOL.int) {
                    optList = parsePoolExchange.getTxPools(jsonObject) as ArrayList<Any>
                }
                if (!jsonObject.isNull("payload")) payloadByte = jsonObject.getString("payload")
                if (payloadByte == "") payloadByte = null
            }, fun(any: Any, type: Int) {
                /*     if (
                         type == TransactionTypes.ADD_LIMIT_ORDER.int
                         || type == TransactionTypes.TypeCreateMultisig.int
                     ) {*/
                optList = any
//                }
            }
        )

        if (transaction != null) {
            val transactionRaw = TransactionRaw(
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
                transaction.gasPrice,
                transaction.commission,
//                -1,
                transaction.payload,
                transaction.gas,
                gascoin,
                transaction.optDouble,
                transaction.optString,
                optList,
                payloadByte,
                transaction.code
            )
            return transactionRaw
        }
        return null
    }

    fun get(
        result: JSONObject, height: Long,
        getCoin: ((gas_coin_id: Long?, symbol: String?) -> CoinObjClass.CoinObj),
        getCoin2: ((gas_coin_id: Long, symbol: String?) -> CoinObjClass.CoinObj),
        getGasCoin: ((gas_coin_id: Long, symbol: String?) -> CoinObjClass.CoinObj),
        getFromWallet: ((address: String) -> Long),
        getToWallet: ((address: String) -> Long),
        getNode: ((address: String) -> Int),
        getCreateCoin: ((jsonObject: JSONObject, tagsObject: JSONObject, address: String) -> CoinObjClass.CoinObj?)? = null,
        getMultisendItem: ((multisendItemRaw: MinterRaw.MultisendItemRaw) -> MinterRaw.MultisendItemRaw)? = null,//Deprecated
        getOther: ((jsonObject: JSONObject, type: Int) -> Unit), //Deprecated
        getData: ((any: Any, type: Int) -> Unit)? = null,
    ): Minter.Transaction? {
        var transaction: Minter.Transaction? = null
        if (!result.isNull("code")) {
            if (result.getString("code") == "0") {

                val type = result.getInt("type")
                val gas_price = result.getInt("gas_price")
                val gas = result.getInt("gas")
                val gasCoinObj = result.getJSONObject("gas_coin");
                val gas_coin_str = gasCoinObj.getString("symbol")
                val gas_coin_id = gasCoinObj.getString("id").toLong()

                val gas_coin = getGasCoin(gas_coin_id, gas_coin_str)

                val payload = if (!result.isNull("payload")) {
                    if (result.getString("payload") == "") false else true
                } else {
                    false
                }
                val hash = result.getString("hash")

                val fromStr = result.getString("from")

                val code = result.getInt("code")
//            val from: Long
                val from = if (type == counters.minter.sdk.minter.TransactionTypes.TypeRedeemCheck) {
                    val fromStrRedeemCheck = "Mx" + result.getJSONObject("tags").getString("tx.from")
                    getFromWallet(fromStrRedeemCheck)
                } else {
                    getFromWallet(fromStr)
                }

                var to: Long? = null
                var stake: String? = null
                var coin: CoinObjClass.CoinObj? = null
                var coin2: CoinObjClass.CoinObj? = null
                var node: Int? = null
                var amount: Double? = null

//                var commission: Int? = null
                var commission: Double? = null

                var optDouble: Double? = null
                var optString: String? = null


                val tags = if (result.isNull("tags")) null else result.getJSONObject("tags")

                commission = minterMatch.getAmount(tags!!.getString("tx.commission_in_base_coin"))

                if (type == counters.minter.sdk.minter.TransactionTypes.TypeMultiSend) {
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
                                CoinObjClass.CoinObj(coinInMultisend, coinInMultisendSymbol)
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

                    val coinObjMap = mutableMapOf<Long, CoinObjClass.CoinObj>()
                    if (type == TransactionTypes.BUY_SWAP_POOL.int || type == TransactionTypes.SELL_SWAP_POOL.int || type == TransactionTypes.SELL_ALL_SWAP_POOL.int) {
                        val jsonCoins = data.getJSONArray("coins")

                        jsonCoins?.forEach {
                            CoinObjClass.fromJson(it as JSONObject)?.let { coin ->
//                coinObj.add(coin)
                                coinObjMap.put(coin.id, coin)
                            }
                        }
                    }
//            println("TransactionTypes $type $data")

                    if (type == TransactionTypes.TypeSend.int) {
                        to = getToWallet(data.getString("to"))
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeDelegate || type == counters.minter.sdk.minter.TransactionTypes.TypeUnbond) {
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                        node = getNode(data.getString("pub_key"))

                        val _coin = data.getJSONObject("coin")
                        coin = CoinObjClass.CoinObj(_coin.getString("id").toLong(), _coin.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeSetCandidateOnline || type == counters.minter.sdk.minter.TransactionTypes.TypeSetCandidateOffline) {
                        node = getNode(data.getString("pub_key"))
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeDeclareCandidacy) {
                        node = getNode(data.getString("pub_key"))
                        stake = data.getString("stake")
                        amount = minterMatch.getAmount(stake)
//                        commission = minterMatch.getAmount(data.getString("commission"))
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeCreateCoin) {
                        getCreateCoin?.invoke(data, tags!!, fromStr)
                        stake = data.getString("initial_amount")
                        amount = minterMatch.getAmount(stake)
                        val coinSymbol_tmp = data.getString("symbol")
                        val coinId_tmp = tags!!.getString("tx.coin_id").toLong()
                        getCoin(coinId_tmp, coinSymbol_tmp)
                        coin = CoinObjClass.CoinObj(coinId_tmp, coinSymbol_tmp)
                        optDouble = minterMatch.getAmount(data.getString("initial_reserve"))

                    } else if (type == TransactionTypes.TypeRecreateCoin.int) {
//                        val tags =result.getJSONObject("tags")
                        getCreateCoin?.invoke(data, tags!!, fromStr)
//                        stake = data.getString("initial_amount")
//                        amount = minterMatch.getAmount(stake)

                        val coinSymbol_tmp = data.getString("symbol")
                        val coinId_tmp = tags!!.getString("tx.coin_id").toLong()
                        getCoin(coinId_tmp, coinSymbol_tmp)
                        coin = CoinObjClass.CoinObj(coinId_tmp, coinSymbol_tmp)

                        val coin2Symbol_tmp = tags!!.getString("tx.old_coin_symbol")
                        val coin2Id_tmp = tags!!.getString("tx.old_coin_id").toLong()
                        getCoin2(coin2Id_tmp, coin2Symbol_tmp)
                        coin2 = CoinObjClass.CoinObj(coin2Id_tmp, coin2Symbol_tmp)

                        optDouble = minterMatch.getAmount(data.getString("initial_reserve"))

                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeSellAllCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObjClass.CoinObj(
                            coin_to_sell.getString("id").toLong(),
                            coin_to_sell.getString("symbol")
                        )
                        coin2 =
                            CoinObjClass.CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                        this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = tags!!.getString("tx.sell_amount")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags.getString("tx.return"))
                        /*tags	tx.sell_amount*/
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeBuyCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin =
                            CoinObjClass.CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        coin2 = CoinObjClass.CoinObj(
                            coin_to_sell.getString("id").toLong(),
                            coin_to_sell.getString("symbol")
                        )
                        coin = this.getCoin(coin.id, coin.symbol, getCoin)
                        coin2 = this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_buy")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeSellCoin) {
                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObjClass.CoinObj(
                            coin_to_sell.getString("id").toLong(),
                            coin_to_sell.getString("symbol")
                        )
                        coin2 =
                            CoinObjClass.CoinObj(coin_to_buy.getString("id").toLong(), coin_to_buy.getString("symbol"))
                        this.getCoin(coin.id, coin.symbol, getCoin)
                        this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_sell")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeEditCandidate) {
                        val nodeStr = data.getString("pub_key")
                        node = getNode(nodeStr)
//                        to = getToWallet(data.getString("reward_address"))
                        val optList = DataEditCandidate(
                            pub_key = nodeStr,
                            reward_address = data.getString("reward_address"),
                            owner_address = data.getString("owner_address"),
                            control_address = data.getString("control_address"),
                        )
                        getData?.invoke(optList, type)

                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeRedeemCheck) {
                        to = getToWallet("Mx" + result.getJSONObject("tags").getString("tx.to"))
//                        coin = this.getCoin(result.getJSONObject("tags").getString("tx.coin"), getCoin) // TODO Найти транзакцию с чеком
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeSetHaltBlock) {
                        node = getNode(data.getString("pub_key"))
                        val haltHeight = data.getLong("height")
                        getData?.invoke(haltHeight, type)
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeEditCoinOwner) { // @TODO Error В TypeEditCoinOwner вообще нет инфы о CoinId, только Symbol
                        to = getToWallet(data.getString("new_owner"))
                        val coinSymbol_tmp = data.getString("symbol")
//                        val tags =result.getJSONObject("tags")
                        val coinId_tmp = tags.getLong("tx.coin_id")
                        coin = getCoin(coinId_tmp, coinSymbol_tmp)
//                        coin = CoinObj(null, coinSymbol_tmp)
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypePriceVote) {
                        TODO()
                        val price = data.getString("price")
                        optString = price
                        optDouble = (price.toDouble() / 1000.0)
                    } else if (type == counters.minter.sdk.minter.TransactionTypes.TypeEditCandidatePublicKey) {
                        node = getNode(data.getString("pub_key"))
                        optString = data.getString("new_pub_key")
                    } else if (type == TransactionTypes.BUY_SWAP_POOL.int) {
                        val coin_to_sell = tags!!.getLong("tx.coin_to_sell")
                        val coin_to_buy = tags.getLong("tx.coin_to_buy")
                        coin = coinObjMap[coin_to_buy]!!
                        coin2 = coinObjMap[coin_to_sell]!!
                        getCoin(coin.id, coin.symbol)
                        getCoin2(coin2.id, coin2.symbol)
//                        coin = this.getCoin(coin.id, coin.symbol, getCoin)
//                        coin2 = this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_buy")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                        optString = data.getString("maximum_value_to_sell")

                    } else if (type == TransactionTypes.SELL_SWAP_POOL.int) {
                        val coin_to_sell = tags!!.getLong("tx.coin_to_sell")
                        val coin_to_buy = tags.getLong("tx.coin_to_buy")
                        coin = coinObjMap[coin_to_sell]!!
                        coin2 = coinObjMap[coin_to_buy]!!
                        getCoin(coin.id, coin.symbol)
                        getCoin2(coin2.id, coin2.symbol)
//                        coin = this.getCoin(coin.id, coin.symbol, getCoin)
//                        coin2 = this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = data.getString("value_to_sell")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                        optString = data.getString("minimum_value_to_buy")
                    } else if (type == TransactionTypes.SELL_ALL_SWAP_POOL.int) {
                        val coin_to_sell = tags.getLong("tx.coin_to_sell")
                        val coin_to_buy = tags.getLong("tx.coin_to_buy")
                        coin = coinObjMap[coin_to_sell]!!
                        coin2 = coinObjMap[coin_to_buy]!!
                        getCoin(coin.id, coin.symbol)
                        getCoin2(coin2.id, coin2.symbol)
//                        coin = this.getCoin(coin.id, coin.symbol, getCoin)
//                        coin2 = this.getCoin(coin2.id, coin2.symbol, getCoin2)
                        stake = tags!!.getString("tx.sell_amount")
//                        commission = minterMatch.getAmount(tags!!.getString("tx.commission_amount"))
//                        commission = minterMatch.getAmount(tags!!.getString("tx.commission_in_base_coin"))
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags!!.getString("tx.return"))
                        optString = data.getString("minimum_value_to_buy")
                    } else if (type == TransactionTypes.ADD_LIMIT_ORDER.int) {

                        val coin_to_sell = data.getJSONObject("coin_to_sell")
                        val coin_to_buy = data.getJSONObject("coin_to_buy")
                        coin = CoinObjClass.fromJson(coin_to_sell)
                        coin2 = CoinObjClass.fromJson(coin_to_buy)
                        getCoin(coin!!.id, coin!!.symbol)
                        getCoin2(coin2!!.id, coin2!!.symbol)

                        stake = data.getString("value_to_sell")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(data.getString("value_to_buy"))

                        val limitOrderRaw = LimitOrderRaw(
                            id = tags.getLong("tx.order_id"),
                            coinSell = coin,
                            wantSell = amount,
                            coinBuy = coin2,
                            wantBuy = optDouble,
                            price = optDouble / amount,
                            owner = fromStr,
                            height = height,
                            pool_id = tags.getLong("tx.pool_id")
                        )
                        getData?.invoke(limitOrderRaw, type)

                    } else if (type == TransactionTypes.TypeCreateMultisig.int) {
                        val created_multisig = tags.getString("tx.created_multisig")
                        val optList = convertMultisig.get2(
                            data.getJSONArray("addresses").toList() as List<String>,
                            data.getJSONArray("weights").toList() as List<String>,
                            data.getLong("threshold"),
                            created_multisig
                        )
                        getData?.invoke(optList, type)
                    } else if (type == TransactionTypes.TypeEditMultisig.int) {
                        val optList = convertMultisig.get2(
                            data.getJSONArray("addresses").toList() as List<String>,
                            data.getJSONArray("weights").toList() as List<String>,
                            data.getLong("threshold"),
                            fromStr
                        )
                        getData?.invoke(optList, type)
                    } else if (type == TransactionTypes.ADD_LIQUIDITY.int) {
                        coin = CoinObjClass.fromJson(data.getJSONObject("coin0"))
                        coin2 = CoinObjClass.fromJson(data.getJSONObject("coin1"))
                        getCoin(coin!!.id, coin!!.symbol)
                        getCoin2(coin2!!.id, coin2!!.symbol)

                        stake = data.getString("volume0")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags.getString("tx.volume1"))
// TODO add DataAddLiquidity
                    } else if (type == TransactionTypes.REMOVE_LIQUIDITY.int) {
                        coin = CoinObjClass.fromJson(data.getJSONObject("coin0"))
                        coin2 = CoinObjClass.fromJson(data.getJSONObject("coin1"))
                        getCoin(coin!!.id, coin!!.symbol)
                        getCoin2(coin2!!.id, coin2!!.symbol)

                        stake = tags.getString("tx.volume0")
                        amount = minterMatch.getAmount(stake)
                        optDouble = minterMatch.getAmount(tags.getString("tx.volume1"))
// TODO add DataAddLiquidity
                    } else if (type == TransactionTypes.EDIT_CANDIDATE_COMMISSION.int) {
                        node = getNode(data.getString("pub_key"))
                        val data_commission = data.getString("commission")
                        optString = data_commission
                        optDouble = data_commission.toDouble()
                    } else if (type == TransactionTypes.MINT_TOKEN.int) {
                        coin = CoinObjClass.fromJson(data.getJSONObject("coin"))
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                    } else if (type == TransactionTypes.BURN_TOKEN.int) {
                        coin = CoinObjClass.fromJson(data.getJSONObject("coin"))
                        stake = data.getString("value")
                        amount = minterMatch.getAmount(stake)
                    } else if (type == TransactionTypes.CREATE_TOKEN.int) {
                        val initialAmount = data.getString("initial_amount")
                        val coinId = tags!!.getLong("tx.coin_id")
                        coin = CoinObjClass.CoinObj(coinId, data.getString("symbol"))
                        getCoin(coin.id, coin.symbol)
                        optString = initialAmount
                        optDouble = minterMatch.getAmount(initialAmount)
                        // TODO add DataRecreateCoin
                    } else if (type == TransactionTypes.RECREATE_TOKEN.int) {
                        val initialAmount = data.getString("initial_amount")
                        val coinId = tags!!.getLong("tx.coin_id")
                        coin = CoinObjClass.CoinObj(coinId, data.getString("symbol"))
                        getCoin(coin.id, coin.symbol)
                        optString = initialAmount
                        optDouble = minterMatch.getAmount(initialAmount)
                        // TODO add DataRecreateCoin
                    } else if (type == TransactionTypes.VOTE_COMMISSION.int) {
                        node = getNode(data.getString("pub_key"))
//                        coin = CoinObjClass.fromJson(data.getJSONObject("coin"))
                        val dataHeight = data.getString("height")
                        optString = dataHeight
                        optDouble = dataHeight.toDouble()
//                        stake = "0"
//                        amount = 0.0

                        val array = arrayListOf<Commission>()
                        var successNum = 0
                        CommissionKey.values().forEach {
                            data.getString(it.key)?.let { value ->
//                                if (successNum==44) return@forEach
                                array.add(Commission(it, minterMatch.getAmount(value)))
                                successNum++
                            }
                        }
//                        println("$successNum ${CommissionKey.values().count()}")
                        getData?.invoke(array, type)
                    } else if (type == TransactionTypes.VOTE_UPDATE.int) {
                        node = getNode(data.getString("pub_key"))
                        optString = data.getString("version")
                        optDouble = data.getDouble("height")
                    } else if (type == TransactionTypes.CREATE_SWAP_POOL.int) {
                        val parsePool = ParsePool()
                        stake = data.getString("volume0")
                        parsePool.getRaw(result)?.let { swapPool ->
                            coin = swapPool.coin0
                            coin2 = swapPool.coin1
                            getCoin(coin!!.id, coin!!.symbol)
                            getCoin2(coin2!!.id, coin2!!.symbol)
                            amount = swapPool.volume0
                            optDouble = swapPool.volume1
                            getData?.invoke(swapPool, type)
                        } ?: run {
                            throw Exception("parsePool.getRaw($result)")
                        }
                    } else if (type == TransactionTypes.REMOVE_LIMIT_ORDER.int) {
                        val orderId = data.getString("id")
                        optString = orderId
                        optDouble = orderId.toDouble()
                        getData?.invoke(orderId.toLong(), type)
                    } else {
                        throw Exception("unknown transaction type: $type")
                    }

                    if (
                        coin == null
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeCreateCoin
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeEditCandidate
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeSetCandidateOnline
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeSetCandidateOffline
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeRedeemCheck
                        && type != counters.minter.sdk.minter.TransactionTypes.TypeCreateMultisig
                        && type < counters.minter.sdk.minter.TransactionTypes.TypeEditCandidate
                    ) {
//                        println("height $height hash $hash result $result data $data")
                        val _coin = data.getJSONObject("coin")
                        coin = CoinObjClass.CoinObj(_coin.getString("id").toLong(), _coin.getString("symbol"))
                        if (coin!!.symbol == Conf.defaultCoin) {
                            getCoin(coin!!.id, coin!!.symbol)
                        } else {
                            this.getCoin(coin!!.id, coin!!.symbol, getCoin)
                        }
                    }
                }
                getOther.invoke(result, type)
//                val commissionCoinId: Long = -1
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
//                    commissionCoinId,
                    payload,
                    gas,
                    gas_coin,
                    optDouble,
                    optString,
                    code
                )
            }
        }
        return transaction
    }

    fun getCoin(gas_coin_id: Long, symbol: String?, getCoin: ((gas_coin_id: Long, symbol: String?) -> CoinObjClass.CoinObj)): CoinObjClass.CoinObj {
//        if (symbol != Conf.defaultCoin)
        return getCoin(gas_coin_id, symbol)
//        return Conf.defaultCoinUid
    }
}