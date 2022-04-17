package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.AddressBalance
import counters.minter.grpc.client.AddressDelegatedBalance
import counters.minter.grpc.client.AddressResponse
import counters.minter.grpc.client.Multisig
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.DataMultisig
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.AddressRaw

class ConvertAddress: MinterMatch() {

//    companion object  {
//        fun get(data: SellAllSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)
//        fun get(data: SellSwapPoolData, tags: MutableMap<String, String>) = get(data.coinsList, tags)

        fun get(response: AddressResponse, address: String): AddressRaw {
//            getListBalance(response.balanceList)
            return AddressRaw(
                address = address,
                count_txs = response.transactionCount,
                balance = getListBalance(response.balanceList),
                delegated = getListDelegated(response.delegatedList),
                total = getListBalance(response.totalList),
                bip_value = getAmount(response.bipValue),
                multisig = getMultisig(response.multisig, address)
            )
        }

    private fun getMultisig(multisig: Multisig, address: String): DataMultisig? {
//        println("multisig ${multisig.weightsCount} ${multisig.addressesCount}")
        val _multisig = if (multisig.weightsCount==0) {
            null
        } else {
//            result.getJSONObject("multisig").let {
            multisig.let {
                val addresses = mutableMapOf<String, Long>()
                val arrWeights = it.weightsList
                var weightsSum = 0L
//                it.getJSONArray("addresses").forEachIndexed { index, _address ->
                it.addressesList.forEachIndexed { index, _address ->
                    val weight = arrWeights[index]
                    weightsSum += weight
                    addresses.put(_address as String, weight)
                }
                DataMultisig(
                    addresses = addresses,
                    threshold = it.threshold,
                    weightsSum = weightsSum,
                    multisig = address,
                )
            }
        }
        return _multisig
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