package counters.minter.sdk.minter.enum

enum class QueryTags(val str: String) {
    TagsTxFrom("tags.tx.from"),
    TagsTxTo("tags.tx.to"),
    TagsTxType("tags.tx.type"),
    TagsTxOrderId("tags.tx.order_id"),
    TagsTxPoolId("tags.tx.pool_id"),
}