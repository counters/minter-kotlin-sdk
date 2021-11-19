package counters.minter.sdk.MinterApi.convert

import com.google.common.io.BaseEncoding
import counters.minter.grpc.client.*
import counters.minter.sdk.Minter.CoinObjClass
import counters.minter.sdk.Minter.Conf
import counters.minter.sdk.Minter.Enum.TransactionTypes
import counters.minter.sdk.Minter.MinterMatch
import counters.minter.sdk.Minter.MinterRaw
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
class ConvertTransaction {
    private var minterMatch = MinterMatch()
    private val logger = KotlinLogging.logger {}
    private var multiSendAdv = MultiSendAdv()

    fun get(transaction: TransactionResponse): MinterRaw.TransactionRaw {
        val type = transaction.type.toInt()
        var to: String? = null
        var node: String? = null
        var coin: CoinObjClass.CoinObj? = null
        var coin2: CoinObjClass.CoinObj? = null
        var stake: String? = null
        var amount: Double? = null

        var optString: String? = null
        var optDouble: Double? = null
        var optList: ArrayList<Any>?=null
        val tags = transaction.tagsMap

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
            }
            TransactionTypes.TypeSellAllCoin.int -> {
                val data = transaction.data.unpack(SellAllCoinData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
            }
            TransactionTypes.TypeBuyCoin.int -> {
                val data = transaction.data.unpack(BuyCoinData::class.java)
                coin = CoinObjClass.CoinObj(data.coinToBuy.id, data.coinToBuy.symbol)
                coin2 = CoinObjClass.CoinObj(data.coinToSell.id, data.coinToSell.symbol)
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
                optList = arrayListOf()
                optList.addAll(multiSendAdvObj.optList)
                coin=multiSendAdvObj.coin
                amount=multiSendAdvObj.amount

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

                val coinIdToSymbol = CoinIdToSymbol(data.coinsList)

//                logger.error { "data: $data" }
                if (coin_to_sell != null && coin_to_buy != null && tx_return != null ) {
                    coin = CoinObjClass.CoinObj(coin_to_sell, coinIdToSymbol[coin_to_sell])
                    coin2 = CoinObjClass.CoinObj(coin_to_buy, coinIdToSymbol[coin_to_buy])
                    optString = tx_return
                    optDouble = minterMatch.getAmount(tx_return)
                    stake = data.valueToSell
                } else {
                    throw Exception("unknown transaction type: $type")
                }
            }
            TransactionTypes.BUY_SWAP_POOL.int -> {
    //            val data = transaction.data.unpack(SendData::class.java)
            }
            TransactionTypes.SELL_ALL_SWAP_POOL.int -> {
                val data = transaction.data.unpack(SellAllSwapPoolData::class.java)


                val coin_to_sell = tags["tx.coin_to_sell"]?.toLong()
                val coin_to_buy = tags["tx.coin_to_buy"]?.toLong()
                val tx_return = tags["tx.return"]

                if (coin_to_sell != null && coin_to_buy != null && tx_return != null ) {
                    coin = CoinObjClass.CoinObj(coin_to_sell, "")
                    coin2 = CoinObjClass.CoinObj(coin_to_buy, "")
                    optString = tx_return
                    optDouble = minterMatch.getAmount(tx_return)
//                    stake = data.minimumValueToBuy
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
            else -> {
                throw Exception("unknown transaction type: $type")
            }
        }
        if (amount == null) amount = if (stake != null) minterMatch.getAmount(stake) else null

        val base64Payload =  try {
            BaseEncoding.base64().encode(transaction.payload.toByteArray())
        } catch (e: Exception) {
            logger.error { "Exception: $e" }
            null
        }
        val payload = !transaction.payload.isEmpty


        return MinterRaw.TransactionRaw(
            hash = transaction.hash,
            height = transaction.height,
            type = type,
            from = transaction.from,
            to = to,
            node = node,
            stake = stake,
            coin = coin,
            coin2 = coin2,
            amount = amount,
            gas_price = transaction.gasCoin.id.toInt(),
            commission = -1.0,
            payload = payload,
            gas = transaction.gas.toInt(),
            gascoin = CoinObjClass.CoinObj(transaction.gasCoin.id, transaction.gasCoin.symbol),
            optDouble = optDouble,
            optString = optString,
            optList = optList,
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