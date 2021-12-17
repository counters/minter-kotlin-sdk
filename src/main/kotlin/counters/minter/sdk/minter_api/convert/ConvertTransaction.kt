package counters.minter.sdk.minter_api.convert

import com.google.common.io.BaseEncoding
import counters.minter.grpc.client.*
import counters.minter.grpc.client.Coin
import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Models.DataEditCandidate
import counters.minter.sdk.minter.Models.TransactionRaw
import mu.KotlinLogging

class ConvertTransaction : MinterMatch() {
    private var minterMatch = MinterMatch()
    private val logger = KotlinLogging.logger {}
    private var multiSendAdv = MultiSendAdv()
    private val convertTxPools = ConvertTxPools
    private val convertMultisig = ConvertMultisig

    fun get(transaction: TransactionResponse): TransactionRaw {
        val type = transaction.type.toInt()
        var to: String? = null
        var node: String? = null
        var coin: CoinObjClass.CoinObj? = null
        var coin2: CoinObjClass.CoinObj? = null
        var stake: String? = null
        var amount: Double? = null

        var optString: String? = null
        var optDouble: Double? = null
//        var optList: ArrayList<Any>?=null
        var optList: Any? = null
        val tags = transaction.tagsMap

        var from = transaction.from

        when (type) {
            TransactionTypes.TypeSend.int -> {
                val data = transaction.data.unpack(SendData::class.java)
                to = data.to
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
                stake = data.value
            }
            TransactionTypes.TypeSellCoin.int -> {
                val data = transaction.data.unpack(SellCoinData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
                stake = data.valueToSell
                optDouble = getAmount(tags["tx.return"]!!)
            }
            TransactionTypes.TypeSellAllCoin.int -> {
                val data = transaction.data.unpack(SellAllCoinData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
                stake = tags["tx.sell_amount"]!!
                optDouble = getAmount(tags["tx.return"]!!)
            }
            TransactionTypes.TypeBuyCoin.int -> {
                val data = transaction.data.unpack(BuyCoinData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
                stake = data.valueToBuy
                optDouble = getAmount(tags["tx.return"]!!)
            }
            TransactionTypes.TypeCreateCoin.int -> {
                val data = transaction.data.unpack(CreateCoinData::class.java)
                stake = data.initialAmount
                optDouble = getAmount(data.initialReserve)
                coin = CoinObjClass.CoinObj(tags["tx.coin_id"]!!.toLong(), tags["tx.coin_symbol"])
            }
            TransactionTypes.TypeDeclareCandidacy.int -> {
                val data = transaction.data.unpack(DeclareCandidacyData::class.java)
                stake = data.stake
                node = data.pubKey
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
            }
            TransactionTypes.TypeDelegate.int -> {
                val data = transaction.data.unpack(DelegateData::class.java)
                node = data.pubKey
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
                stake = data.value
            }
            TransactionTypes.TypeUnbond.int -> {
                val data = transaction.data.unpack(UnbondData::class.java)
                node = data.pubKey
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
                stake = data.value

            }
            TransactionTypes.TypeRedeemCheck.int -> {
//                val data = transaction.data.unpack(RedeemCheckData::class.java)
//                val byteArrayCheck = Base64.getDecoder().decode(data.rawCheck) //TODO(decode amount and coin)
//                println(byteArrayCheck.toString() )
                to = "Mx" + tags["tx.to"]
                from = "Mx" + tags["tx.from"]
            }
            TransactionTypes.TypeSetCandidateOnline.int -> {
                val data = transaction.data.unpack(SetCandidateOnData::class.java)
                node = data.pubKey

            }
            TransactionTypes.TypeSetCandidateOffline.int -> {
                val data = transaction.data.unpack(SetCandidateOffData::class.java)
                node = data.pubKey
            }
            TransactionTypes.TypeCreateMultisig.int -> {
                val data = transaction.data.unpack(CreateMultisigData::class.java)
                val created_multisig = tags["tx.created_multisig"]!!
                optList = convertMultisig.get(data.addressesList, data.weightsList, data.threshold, created_multisig)
            }
            TransactionTypes.TypeEditMultisig.int -> {
                val data = transaction.data.unpack(EditMultisigData::class.java)
                optList = convertMultisig.get(data.addressesList, data.weightsList, data.threshold, from)
            }
            TransactionTypes.TypeMultiSend.int -> {
                val data = transaction.data.unpack(MultiSendData::class.java)
                val multiSendAdvObj = multiSendAdv.get(data)
//                optList = arrayListOf()
                optList = arrayListOf<MinterRaw.MultisendItemRaw>()
                optList.addAll(multiSendAdvObj.optList)
                coin = multiSendAdvObj.coin
                amount = multiSendAdvObj.amount

            }
            TransactionTypes.TypeEditCandidate.int -> {
                val data = transaction.data.unpack(EditCandidateData::class.java)
                node = data.pubKey
//                to = data.ownerAddress
                optList = DataEditCandidate(
                    pub_key = data.pubKey,
                    reward_address = data.rewardAddress,
                    owner_address = data.ownerAddress,
                    control_address = data.controlAddress,
                )
            }
            TransactionTypes.TypeSetHaltBlock.int -> {
                val data = transaction.data.unpack(SetHaltBlockData::class.java)
                node = data.pubKey
                optList = data.height
            }
            TransactionTypes.TypeRecreateCoin.int -> {
                val data = transaction.data.unpack(RecreateCoinData::class.java)
                coin = CoinObjClass.CoinObj(tags["tx.coin_id"]!!.toLong(), data.symbol)
                coin2 = CoinObjClass.CoinObj(tags["tx.old_coin_id"]!!.toLong(), tags["tx.old_coin_symbol"])
                optDouble = getAmount(data.initialReserve)
                // TODO add DataRecreateCoin
            }
            TransactionTypes.TypeEditCoinOwner.int -> {
                val data = transaction.data.unpack(EditCoinOwnerData::class.java)
                to = data.newOwner
                coin = CoinObjClass.CoinObj(tags["tx.coin_id"]!!.toLong(), data.symbol)
            }
            TransactionTypes.TypePriceVote.int -> {
                TODO()
            }
            TransactionTypes.TypeEditCandidatePublicKey.int -> {
                val data = transaction.data.unpack(EditCandidatePublicKeyData::class.java)
//                "pub_key": "Mp5919a1946dc2c91886ad09deff5ec073fd81b322e0e0d7ad0349315c66d6c8fd",
//                "new_pub_key": "Mp32b95bac78d18d840783dd119a2e830f0e9374507eaee49a1056435ebc75decc"
                node = data.pubKey
                optString = data.newPubKey

            }
            TransactionTypes.ADD_LIQUIDITY.int -> {
                val data = transaction.data.unpack(AddLiquidityData::class.java)
                coin = CoinObjClass.CoinObj(data.coin0.id, data.coin0.symbol)
                coin2 = CoinObjClass.CoinObj(data.coin1.id, data.coin1.symbol)
                stake = data.volume0
                optDouble = getAmount(tags["tx.volume1"]!!)
                // TODO add DataAddLiquidity
            }
            TransactionTypes.REMOVE_LIQUIDITY.int -> {
                val data = transaction.data.unpack(RemoveLiquidityData::class.java)
                coin = CoinObjClass.CoinObj(data.coin0.id, data.coin0.symbol)
                coin2 = CoinObjClass.CoinObj(data.coin1.id, data.coin1.symbol)
                stake = tags["tx.volume0"]
                optDouble = getAmount(tags["tx.volume1"]!!)
                // TODO add DataAddLiquidity
            }
            TransactionTypes.SELL_SWAP_POOL.int -> {
                val data = transaction.data.unpack(SellSwapPoolData::class.java)
                val coin_to_sell = tags["tx.coin_to_sell"]?.toLong()
                val coin_to_buy = tags["tx.coin_to_buy"]?.toLong()
                val tx_return = tags["tx.return"]
                val minimum_value_to_buy = data.minimumValueToBuy

                val coinIdToSymbol = CoinIdToSymbol(data.coinsList)
//                logger.error { "data: $data" }
                if (coin_to_sell != null && coin_to_buy != null && tx_return != null) {
                    coin = CoinObjClass.CoinObj(coin_to_sell, coinIdToSymbol[coin_to_sell])
                    coin2 = CoinObjClass.CoinObj(coin_to_buy, coinIdToSymbol[coin_to_buy])
                    optString = minimum_value_to_buy
                    optDouble = minterMatch.getAmount(tx_return)
                    stake = data.valueToSell
                } else {
                    throw Exception("unknown")
                }

                optList = convertTxPools.get(data.coinsList, tags)

            }
            TransactionTypes.BUY_SWAP_POOL.int -> {
                val data = transaction.data.unpack(BuySwapPoolData::class.java)
                val coin_to_buy = tags["tx.coin_to_sell"]?.toLong()
                val coin_to_sell = tags["tx.coin_to_buy"]?.toLong()
                val tx_return = tags["tx.return"]

                val coinIdToSymbol = CoinIdToSymbol(data.coinsList)
//                logger.error { "data: $data" }
                if (coin_to_sell != null && coin_to_buy != null && tx_return != null) {
                    coin = CoinObjClass.CoinObj(coin_to_sell, coinIdToSymbol[coin_to_sell])
                    coin2 = CoinObjClass.CoinObj(coin_to_buy, coinIdToSymbol[coin_to_buy])
                    optString = data.maximumValueToSell
                    optDouble = minterMatch.getAmount(tx_return)
                    stake = data.valueToBuy
                } else {
                    throw Exception("unknown")
                }

                optList = convertTxPools.get(data.coinsList, tags)

            }
            TransactionTypes.SELL_ALL_SWAP_POOL.int -> {
                val data = transaction.data.unpack(SellAllSwapPoolData::class.java)

                val coin_to_sell = tags["tx.coin_to_sell"]?.toLong()
                val coin_to_buy = tags["tx.coin_to_buy"]?.toLong()
                val tx_return = tags["tx.return"]

                val coinIdToSymbol = CoinIdToSymbol(data.coinsList)

                if (coin_to_sell != null && coin_to_buy != null && tx_return != null) {
                    coin = CoinObjClass.CoinObj(coin_to_sell, coinIdToSymbol[coin_to_sell])
                    coin2 = CoinObjClass.CoinObj(coin_to_buy, coinIdToSymbol[coin_to_buy])
                    optString = data.minimumValueToBuy
                    optDouble = minterMatch.getAmount(tx_return)

                    stake = tags["tx.sell_amount"]

                    optList = convertTxPools.get(data.coinsList, tags)
                } else {
                    throw Exception("unknown transaction type: $type")
                }
            }
            TransactionTypes.EDIT_CANDIDATE_COMMISSION.int -> {
                val data = transaction.data.unpack(EditCandidateCommission::class.java)
                node = data.pubKey
                val data_commission = data.commission
                optString = data_commission.toString()
                optDouble = data_commission.toDouble()
            }
            TransactionTypes.MOVE_STAKE.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
                TODO()
            }
            TransactionTypes.MINT_TOKEN.int -> {
                val data = transaction.data.unpack(MintTokenData::class.java)
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
                stake = data.value
            }
            TransactionTypes.BURN_TOKEN.int -> {
                val data = transaction.data.unpack(BurnTokenData::class.java)
                coin = CoinObjClass.CoinObj(data.coin.id, data.coin.symbol)
                stake = data.value
            }
            TransactionTypes.CREATE_TOKEN.int -> {
                val data = transaction.data.unpack(CreateTokenData::class.java)
                coin = CoinObjClass.CoinObj(tags["tx.coin_id"]!!.toLong(), data.symbol)
                optDouble = getAmount(data.initialAmount)
                optString = data.initialAmount
                // TODO add DataRecreateCoin
            }
            TransactionTypes.RECREATE_TOKEN.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
                TODO()
            }
            TransactionTypes.VOTE_COMMISSION.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
                TODO()
            }
            TransactionTypes.VOTE_UPDATE.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
                TODO()
            }
            TransactionTypes.CREATE_SWAP_POOL.int -> {
                TODO()
            }
            TransactionTypes.ADD_LIMIT_ORDER.int -> {
                val data = transaction.data.unpack(AddLimitOrderData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
                stake = data.valueToSell
                amount = minterMatch.getAmount(stake)
                optDouble = minterMatch.getAmount(data.valueToBuy)

                optList = LimitOrderRaw(
                    id = tags["tx.order_id"]!!.toLong(),
                    coinSell = coin,
                    wantSell = amount,
                    coinBuy = coin2,
                    wantBuy = optDouble,
                    price = optDouble / amount,
                    owner = from,
                    height = transaction.height,
                    pool_id = tags["tx.pool_id"]!!.toLong()
                )
            }
            TransactionTypes.REMOVE_LIMIT_ORDER.int -> {
                TODO()
            }
            else -> {
                throw Exception("unknown transaction type: $type")
            }
        }
        if (amount == null) amount = if (stake != null) minterMatch.getAmount(stake) else null

        /*      val base64Payload =  try {
                  BaseEncoding.base64().encode(transaction.payload.toByteArray())
              } catch (e: Exception) {
                  logger.error { "Exception: $e" }
                  null
              }*/
        val payload = !transaction.payload.isEmpty

        val base64Payload = if (payload) {
            try {
                BaseEncoding.base64().encode(transaction.payload.toByteArray())
            } catch (e: Exception) {
                logger.error { "Exception: $e" }
                null
            }
        } else {
            null
        }

//        logger.info { "transaction: $transaction" }

        val commission = tags["tx.commission_in_base_coin"]?.let { minterMatch.getAmount(it) } ?: run { -1.0 }
//        val commission = -1.0

//        val commissionCoinId: Long = tags["tx.commission_coin"]!!.toLong()
//        val commissionCoinId: Long = -1

        return TransactionRaw(
            hash = transaction.hash,
            height = transaction.height,
            type = type,
            from = from,
            to = to,
            node = node,
            stake = stake,
            coin = coin,
            coin2 = coin2,
            amount = amount,
            gasPrice = transaction.gasPrice.toInt(),
            commission = commission,
//            commissionCoinId = commissionCoinId,
            payload = payload,
            gas = transaction.gas.toInt(),
            gasCoin = CoinObjClass.CoinObj(transaction.gasCoin.id, transaction.gasCoin.symbol),
            optDouble = optDouble,
            optString = optString,
            optData = optList,
            base64Payload = base64Payload,
        )
    }

    private fun CoinIdToSymbol(coinsList: List<Coin>): Map<Long, String> {
        val map = mutableMapOf<Long, String>()
        coinsList.forEach {
            if (it.symbol == Conf.defaultCoin)
                map[Conf.defaultCoinUid] = Conf.defaultCoin
            else
                map[it.id] = it.symbol
        }
        return map
    }
}