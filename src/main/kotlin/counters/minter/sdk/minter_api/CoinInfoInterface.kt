package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.CoinIdRequest
import counters.minter.grpc.client.CoinInfoRequest
import counters.minter.grpc.client.CoinInfoResponse
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter_api.convert.ConvertCoinInfo
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

//import counters.minter.sdk.minter_api.CoinInfoRequestInterface as CoinInfoRequestInterface1

interface CoinInfoInterface : CoinInfoRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertCoinInfo: ConvertCoinInfo

    val logger: KLogger

    fun getCoinInfoGrpc(request: CoinInfoRequest, deadline: Long?): CoinInfoResponse? {
        try {
            blockingClient.coinInfo(request)?.let {
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

    fun getCoinInfoGrpc(request: CoinIdRequest, deadline: Long?): CoinInfoResponse? {
        try {
            blockingClient.coinInfoById(request)?.let {
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

    fun getCoinInfoGrpc(coin: Long, height: Long? = null, deadline: Long? = null) =
        getCoinInfoGrpc(getRequestCoinInfo(coin, height), deadline)

    fun getCoinInfoGrpc(symbol: String, height: Long? = null, deadline: Long? = null) =
        getCoinInfoGrpc(getRequestCoinInfo(symbol, height), deadline)

    fun getCoinInfoGrpc(request: CoinInfoRequest, deadline: Long? = null, result: (result: CoinInfoResponse?) -> Unit) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.coinInfo(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getCoinInfoGrpc(request: CoinIdRequest, deadline: Long? = null, result: (result: CoinInfoResponse?) -> Unit) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.coinInfoById(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    private fun getCoin(coin: Long, height: Long? = null, deadline: Long? = null): MinterRaw.CoinRaw? {
        TODO("Not yet implemented")
    }

    fun getCoinInfoGrpc(coin: Long, height: Long? = null, deadline: Long? = null, result: ((result: CoinInfoResponse?) -> Unit)) =
        getCoinInfoGrpc(getRequestCoinInfo(coin, height), deadline, result)

    fun getCoinInfoGrpc(symbol: String, height: Long? = null, deadline: Long? = null, result: ((result: CoinInfoResponse?) -> Unit)) =
        getCoinInfoGrpc(getRequestCoinInfo(symbol, height), deadline, result)


    fun getCoinInfo(coin: Long, height: Long? = null, deadline: Long? = null): MinterRaw.CoinRaw? {
        getCoinInfoGrpc(coin, height, deadline)?.let {
            return convertCoinInfo.get(it)
        } ?: run {
            return null
        }
    }

    fun getCoinInfo(symbol: String, height: Long? = null, deadline: Long? = null): MinterRaw.CoinRaw? {
        getCoinInfoGrpc(symbol, height, deadline)?.let {
            return convertCoinInfo.get(it)
        } ?: run {
            return null
        }
    }

    fun getCoinInfo(coin: Long, height: Long? = null, deadline: Long? = null, result: ((MinterRaw.CoinRaw?) -> Unit)) {
        getCoinInfoGrpc(coin, height, deadline) {
            it?.let { result(convertCoinInfo.get(it)) } ?: run { result(null) }
        }
    }

    fun getCoinInfo(symbol: String, height: Long? = null, deadline: Long? = null, result: ((MinterRaw.CoinRaw?) -> Unit)) {
        getCoinInfoGrpc(symbol, height, deadline) {
            it?.let { result(convertCoinInfo.get(it)) } ?: run { result(null) }
        }
    }
}
