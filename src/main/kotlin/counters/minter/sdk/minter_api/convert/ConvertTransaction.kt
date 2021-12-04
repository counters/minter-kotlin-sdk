package counters.minter.sdk.minter_api.convert

import com.google.common.io.BaseEncoding
import counters.minter.grpc.client.*
import counters.minter.grpc.client.Coin
import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.Enum.TransactionTypes
import counters.minter.sdk.minter.Models.TransactionRaw
import mu.KotlinLogging

/*TypeSend(1),
TypeSellCoin(2),
TypeSellAllCoin(3),
TypeBuyCoin(4),
TypeCreateCoin(5),
TypeDeclareCandidacy(6),
TypeDelegate(7),
TypeUnbond(8),
TypeRedeemCheck(9),
TypeSetCandidateOnline(10),
TypeSetCandidateOffline(11),
TypeCreateMultisig(12),
TypeMultiSend(13),
TypeEditCandidate(14),
TypeSetHaltBlock(15),
TypeRecreateCoin(16),
TypeEditCoinOwner(17),
TypeEditMultisig(18),
TypePriceVote(19),
TypeEditCandidatePublicKey(20),
ADD_LIQUIDITY(21),
REMOVE_LIQUIDITY(22),
SELL_SWAP_POOL(23),
BUY_SWAP_POOL(24),
SELL_ALL_SWAP_POOL(25),
EDIT_CANDIDATE_COMMISSION(26),
MOVE_STAKE(27),
MINT_TOKEN(28),
BURN_TOKEN(29),
CREATE_TOKEN(30),
RECREATE_TOKEN(31),
VOTE_COMMISSION(32),
VOTE_UPDATE(33),
CREATE_SWAP_POOL(34),*/
class ConvertTransaction: MinterMatch() {
    private var minterMatch = MinterMatch()
    private val logger = KotlinLogging.logger {}
    private var multiSendAdv = MultiSendAdv()
    private val convertTxPools = ConvertTxPools

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

        val from = transaction.from

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
            }
            TransactionTypes.TypeDeclareCandidacy.int -> {
                val data = transaction.data.unpack(DeclareCandidacyData::class.java)
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
//                val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeSetCandidateOnline.int -> {
//                val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeSetCandidateOffline.int -> {
//                val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeCreateMultisig.int -> {
//                val data = transaction.data.unpack(SendData::class.java)
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
//                val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeSetHaltBlock.int -> {
//                val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeRecreateCoin.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeEditCoinOwner.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeEditMultisig.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypePriceVote.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.TypeEditCandidatePublicKey.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.ADD_LIQUIDITY.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.REMOVE_LIQUIDITY.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
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
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.MOVE_STAKE.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.MINT_TOKEN.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.BURN_TOKEN.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.CREATE_TOKEN.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.RECREATE_TOKEN.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.VOTE_COMMISSION.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.VOTE_UPDATE.int -> {
                //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.CREATE_SWAP_POOL.int -> {

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