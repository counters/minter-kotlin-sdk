package counters.minter.sdk.Minter

import counters.minter.sdk.Minter.Enum.QueryTags
import counters.minter.sdk.Minter.Enum.TransactionTypes as MinterTransactionTypes

class MinterTransactionQuery {

    fun build(query: Map<QueryTags, MinterTransactionTypes>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        query.forEach { queryTags, transactionTypes ->
            map.put(queryTags.str, transactionTypes.int.toString(16))
        }
        return map
    }

}