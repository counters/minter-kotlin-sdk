package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.EstimateCoinBuyRequest
import counters.minter.grpc.client.EstimateCoinBuyResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertEstimateCoinBuy
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface EstimateCoinBuyInterface : EstimateCoinBuyRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub
    val logger: KLogger

    val convertEstimateCoinBuy: ConvertEstimateCoinBuy


    fun getEstimateCoinBuyGrpc(request: EstimateCoinBuyRequest, deadline: Long?): EstimateCoinBuyResponse? {
        try {
            blockingClient.estimateCoinBuy(request)?.let {
                return it
            } ?: run {
                return null
            }
        } catch (e: StatusRuntimeException) {
            logger.warn { e }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getEstimateCoinBuyGrpc(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null,
        deadline: Long? = null
    ) = getEstimateCoinBuyGrpc(getRequestEstimateCoinBuy(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route), deadline)

    fun getEstimateCoinBuyGrpc(request: EstimateCoinBuyRequest, deadline: Long? = null, result: ((result: EstimateCoinBuyResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.estimateCoinBuy(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getEstimateCoinBuyGrpc(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: ((result: EstimateCoinBuyResponse?) -> Unit)
    ) = getEstimateCoinBuyGrpc(getRequestEstimateCoinBuy(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route), deadline, result)

    fun estimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ): Coin.EstimateCoin? {
        getEstimateCoinBuyGrpc(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route, deadline)?.let {
            return convertEstimateCoinBuy.get(it)
        } ?: run {
            return null
        }
    }

    fun estimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: (Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinBuyGrpc(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route, deadline) {
            it?.let { result(convertEstimateCoinBuy.get(it)) } ?: run { result(null) }
        }
    }

}