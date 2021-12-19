package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.*
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.AddressRaw

class ConvertAddress: MinterMatch() {

//    companion object  {
//        fun get(data: SellAllSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)
//        fun get(data: SellSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(response: AddressResponse, address: String): AddressRaw {
            getListBalance(response.balanceList)


            return AddressRaw(
                address = address,
                count_txs = response.transactionCount,
                balance = getListBalance(response.balanceList),
                delegated = getListDelegated(response.delegatedList),
                total = getListBalance(response.balanceList),
                bip_value = getAmount(response.bipValue),
                multisig = null
            )
        }

        private fun getListDelegated(balanceList: List<AddressDelegatedBalance>): List<Minter.Delegated> {
            val arrayBalance = ArrayList<Minter.Delegated>()
            balanceList.forEach {
                val coin = CoinObjClass.CoinObj(it.coin.id, it.coin.symbol)
                val value = getAmount(it.value)
                val bipValue = getAmount(it.bipValue)
                val delegated = getAmount(it.delegateBipValue)
                arrayBalance.add(Minter.Delegated(coin, value, bipValue, delegated))
            }
            return arrayBalance
        }

        private fun getListBalance(balanceList: List<AddressBalance>): List<Minter.Balance> {
            val arrayBalance = ArrayList<Minter.Balance>()
            balanceList.forEach {
                val coin = CoinObjClass.CoinObj(it.coin.id, it.coin.symbol)
                val value = getAmount(it.value)
                val bipValue = getAmount(it.bipValue)
                arrayBalance.add(Minter.Balance(coin, value, bipValue))
            }
            return arrayBalance
        }

//    }
}