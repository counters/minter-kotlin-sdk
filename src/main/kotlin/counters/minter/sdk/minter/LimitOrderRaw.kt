package counters.minter.sdk.minter

data class LimitOrderRaw(
    val id: Long,
    val coinSell: CoinObjClass.CoinObj,
    val wantSell: Double,
    val coinBuy: CoinObjClass.CoinObj,
    val wantBuy: Double,
    val price: Double,
    val owner: String,
    val height: Long,
    val pool_id: Long?
)

/*
{
    "id": "121",
    "coin_sell": {
    "id": "1902",
    "symbol": "HUB"
},
    "coin_buy": {
    "id": "0",
    "symbol": "BIP"
},
    "want_sell": "1000000000000000000",
    "want_buy": "18999900000000000000000",
    "price": "0.0000526318559571366165085079395155",
    "owner": "Mx0903ab168597a7c86ad0d4b72424b3632be0af1b",
    "height": "7696427"
}
*/
