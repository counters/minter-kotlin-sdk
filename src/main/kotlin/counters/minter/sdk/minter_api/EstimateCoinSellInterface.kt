package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.EstimateCoinSellRequest
import counters.minter.grpc.client.EstimateCoinSellResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertEstimateCoinSell
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface EstimateCoinSellInterface: EstimateCoinSellRequestInterface  {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertEstimateCoinSell: ConvertEstimateCoinSell

    val logger: KLogger

      fun getEstimateCoinSellGrpc(request: EstimateCoinSellRequest, deadline: Long?): EstimateCoinSellResponse? {
        try {
            blockingClient.estimateCoinSell(request)?.let {
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

    fun getEstimateCoinSellGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null,
        deadline: Long? = null
    ) = getEstimateCoinSellGrpc(getRequestEstimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route), deadline)

    fun getEstimateCoinSellGrpc(request: EstimateCoinSellRequest, deadline: Long? = null, result: ((result: EstimateCoinSellResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.estimateCoinSell(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getEstimateCoinSellGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: ((result: EstimateCoinSellResponse?) -> Unit)
    ) = getEstimateCoinSellGrpc(getRequestEstimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route), deadline, result)




    fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
//        notFoundCoin: ((notFount: Boolean) -> Unit)? = null
    ): Coin.EstimateCoin? {
        getEstimateCoinSellGrpc(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline)?.let {
            return convertEstimateCoinSell.get(it)
        } ?: run {
            return null
        }
    }
    fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: (Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinSellGrpc(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route, deadline) {
            it?.let { result(convertEstimateCoinSell.get(it)) } ?: run { result(null) }
        }
    }

}
