package counters.minter.sdk.minter

import counters.minter.sdk.minter.Enum.QueryTags
import counters.minter.sdk.minter.Enum.TransactionTypes as MinterTransactionTypes

class MinterTransactionQuery {

    fun build(query: Map<QueryTags, MinterTransactionTypes>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        query.forEach { queryTags, transactionTypes ->
            map.put(queryTags.str, transactionTypes.int.toString(16))
        }
        return map
    }

}