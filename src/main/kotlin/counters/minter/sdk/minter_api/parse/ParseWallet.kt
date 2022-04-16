package counters.minter.sdk.minter_api.parse

import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.DataMultisig
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.AddressRaw
import org.json.JSONArray
import org.json.JSONObject

class ParseWallet {

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    var minterMatch = MinterMatch()

    fun getRaw(result: JSONObject, address: String): AddressRaw? {
//        val balance = mutableMapOf<CoinObjClass.CoinObj, Double>()
        val transaction_count = result.getLong("transaction_count")
        val bip_value = result.getString("bip_value")

        val arrayBalance = if (result.isNull("balance")) null else getListBalance(result.getJSONArray("balance"))
        val arrayDelegated = if (result.isNull("delegated")) null else getListDelegate(result.getJSONArray("delegated"))
        val arrayTotal = if (result.isNull("total")) null else getListBalance(result.getJSONArray("total"))

        val multisig = if (result.isNull("multisig")) {
            null
        } else {
            result.getJSONObject("multisig").let {
                val addresses = mutableMapOf<String, Long>()
                val arrWeights = it.getJSONArray("weights")
                var weightsSum = 0L
                it.getJSONArray("addresses").forEachIndexed { index, _address ->
                    val weight = (arrWeights[index] as String).toLong()
                    weightsSum += weight
                    addresses.put(_address as String, weight)
                }
                DataMultisig(
                    addresses = addresses,
                    threshold = it.getLong("threshold"),
                    weightsSum = weightsSum,
                    multisig = address,
                )
            }
        }

        if (arrayBalance != null && arrayTotal != null)
            return AddressRaw(
                address,
                transaction_count,
                arrayBalance,
                arrayDelegated,
                arrayTotal,
                minterMatch.getAmount(bip_value),
                multisig
            )
        else
            return null

    }

    fun get(result: JSONObject, address: String): Minter.Wallet? {
        getRaw(result, address)?.let {
            return Minter.Wallet(
                null,
                address,
                it.count_txs,
                it.balance,
                it.delegated,
                it.total,
                it.bip_value
            )
        } ?: run {
            return null
        }
    }

    private fun getListBalance(jsonArray: JSONArray): List<Minter.Balance> {
        val arrayBalance = ArrayList<Minter.Balance>()
        jsonArray.forEach {
            it as JSONObject
            val coin = CoinObjClass.fromJson(it.getJSONObject("coin"))
            val value = minterMatch.getAmount(it.getString("value"))
            val bipValue = minterMatch.getAmount(it.getString("bip_value"))
            arrayBalance.add(Minter.Balance(coin!!, value, bipValue))
        }
        return arrayBalance
    }
    private fun getListDelegate(jsonArray: JSONArray): List<Minter.Delegated> {
        val arrayBalance = ArrayList<Minter.Delegated>()
        jsonArray.forEach {
            it as JSONObject
            val coin = CoinObjClass.fromJson(it.getJSONObject("coin"))
            val value = minterMatch.getAmount(it.getString("value"))
            val bipValue = minterMatch.getAmount(it.getString("bip_value"))
            val delegateBip = minterMatch.getAmount(it.getString("delegate_bip_value"))
            arrayBalance.add(Minter.Delegated(coin!!, value, bipValue, delegateBip))
        }
        return arrayBalance
    }


}