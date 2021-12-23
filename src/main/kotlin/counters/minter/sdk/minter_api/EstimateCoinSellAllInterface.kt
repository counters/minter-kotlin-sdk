package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.EstimateCoinSellAllRequest
import counters.minter.grpc.client.EstimateCoinSellAllResponse
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter_api.convert.ConvertEstimateCoinSellAll
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface EstimateCoinSellAllInterface : EstimateCoinSellAllRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertEstimateCoinSellAll: ConvertEstimateCoinSellAll

    val logger: KLogger

    fun getEstimateCoinSellAllGrpc(request: EstimateCoinSellAllRequest, deadline: Long?): EstimateCoinSellAllResponse? {
        try {
            blockingClient.estimateCoinSellAll(request)?.let {
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

    fun getEstimateCoinSellAllGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ) = getEstimateCoinSellAllGrpc(getRequestEstimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route), deadline)

    fun getEstimateCoinSellAllGrpc(request: EstimateCoinSellAllRequest, deadline: Long? = null, result: ((result: EstimateCoinSellAllResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.estimateCoinSellAll(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getEstimateCoinSellAllGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: ((result: EstimateCoinSellAllResponse?) -> Unit)
    ) = getEstimateCoinSellAllGrpc(getRequestEstimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route), deadline, result)

    fun estimateCoinSellAll(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ): Coin.EstimateCoin? {
        getEstimateCoinSellAllGrpc(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline)?.let {
            return convertEstimateCoinSellAll.get(it)
        } ?: run {
            return null
        }
    }

    fun estimateCoinSellAll(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null,
        result: (Coin.EstimateCoin?) -> Unit
    ) {
        getEstimateCoinSellAllGrpc(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route, deadline) {
            it?.let { result(convertEstimateCoinSellAll.get(it)) } ?: run { result(null) }
        }
    }

//    abstract fun getEstimateCoinSellAllGrpc(coinToSell: Long, valueToSell: String, coinToBuy: Long, height: Long?, gasPrice: Int?, swapFrom: SwapFromTypes?, route: List<Long>?, deadline: Long?, function: () -> Unit)
}
