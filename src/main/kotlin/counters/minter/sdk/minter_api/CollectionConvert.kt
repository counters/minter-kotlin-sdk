package counters.minter.sdk.minter_api

import counters.minter.sdk.minter.enums.BlockField

interface CollectionConvert {

    fun conv(fields: List<BlockField>? = null): HashSet<String>? {
        fields?.let {
            val hashSet = hashSetOf<String>()
            it.forEach {
                hashSet.add(it.name)
            }
            return hashSet
        }
        return null
    }

    fun BlockFieldHashSet(fields: List<counters.minter.grpc.client.BlockField>? = null): HashSet<String>? {
        fields?.let {
            val hashSet = hashSetOf<String>()
            it.forEach {
                hashSet.add(it.name)
            }
            return hashSet
        }
        return null
    }

    fun conv(params: List<Pair<String, String>>? = null): Map<String, String>? {
        params?.let {
            val mutableMap = mutableMapOf<String, String>()
            it.forEach {
                mutableMap.put(it.first, it.second)
            }
            return mutableMap
        }
        return null
    }

    fun conv(params: Map<String, String>? = null): List<Pair<String, String>>? {
        params?.let {
            val listPair: ArrayList<Pair<String, String>> = arrayListOf()
            it.forEach {
                listPair.add(Pair(it.key, it.value))
            }
            return listPair
        }
        return null
    }

}
