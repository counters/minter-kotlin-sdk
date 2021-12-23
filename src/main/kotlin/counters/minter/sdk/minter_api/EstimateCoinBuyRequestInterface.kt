package counters.minter.sdk.minter_api

import counters.minter.sdk.minter_api.convert.ConvertSwapFrom

interface EstimateCoinBuyRequestInterface {
    val convertSwapFrom: ConvertSwapFrom
}
