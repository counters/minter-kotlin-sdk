package counters.minter.sdk.minter_api.convert

import counters.minter.sdk.minter.DataMultisig

class ConvertMultisig {

    companion object {

        fun get2(addressesList: List<String>, weightsList: List<String>, threshold: Long, multisig: String): DataMultisig {
            val newWeightsList = weightsList.map { it.toLong() }
           return get(addressesList, newWeightsList, threshold, multisig)
        }

        fun get(addressesList: List<String>, weightsList: List<Long>, threshold: Long, multisig: String): DataMultisig {

            val addresses = mutableMapOf<String, Long>()
            var weightsSum = 0L
            addressesList.forEachIndexed { index, address ->
                val weight = weightsList[index]
                addresses.put(address, weight)
                weightsSum += weight
            }
            return DataMultisig(
                addresses = addresses,
                threshold = threshold,
                weightsSum = weightsSum,
                multisig = multisig
            )
        }
    }
}