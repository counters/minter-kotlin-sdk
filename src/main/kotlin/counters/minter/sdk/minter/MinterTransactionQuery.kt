package counters.minter.sdk.minter

import counters.minter.sdk.minter.enums.QueryTags
import counters.minter.sdk.minter.enums.TransactionTypes as MinterTransactionTypes

@Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
class MinterTransactionQuery {

    @Deprecated(level = DeprecationLevel.WARNING, message = "Deprecated")
    fun build(query: Map<QueryTags, MinterTransactionTypes>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        query.forEach { queryTags, transactionTypes ->
            map.put(queryTags.str, transactionTypes.int.toString(16))
        }
        return map
    }

}
