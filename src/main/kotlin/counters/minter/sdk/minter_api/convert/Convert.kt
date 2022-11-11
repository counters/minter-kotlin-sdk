package counters.minter.sdk.minter_api.convert

class Convert {
    val status = ConvertStatus
    val transaction = ConvertTransaction()
    val block = ConvertBlock()
    val subscribe = ConvertSubscribe
    val limitOrder = ConvertLimitOrder()
    val events = ConvertEvents()
    val address = ConvertAddress()
    val estimateCoinSell = ConvertEstimateCoinSell()
    val estimateCoinSellAll = ConvertEstimateCoinSellAll()
    val estimateCoinBuy = ConvertEstimateCoinBuy()
    val convertSwapPool = ConvertSwapPool()
    val convertCoinInfo = ConvertCoinInfo()
    val convertBestTrade = ConvertBestTrade()
    val convertFrozenAll = ConvertFrozenAll()
    val convertCandidate = ConvertCandidate()

}
