package counters.minter.sdk.minter_api

import counters.minter.grpc.client.*
import counters.minter.sdk.minter.Minter
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter.models.AddressRaw
import counters.minter.sdk.minter_api.convert.ConvertAddress
import counters.minter.sdk.minter_api.convert.ConvertEvents
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface AddressInterface: AddressRequestInterface {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertAddress: ConvertAddress

    val logger: KLogger

    fun getAddressGrpc(request: AddressRequest, deadline: Long?): AddressResponse? {
        try {
            blockingClient.address(request)?.let {
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

    fun getAddressGrpc(address: String, height: Long? = null, delegated: Boolean = false, deadline: Long? = null)
    = getAddressGrpc(getRequestAddress(address, height,delegated), deadline)

    fun getAddressGrpc(request: AddressRequest, deadline: Long? = null, result: ((result: AddressResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.address(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }
    fun getAddressGrpc(address: String, height: Long? = null, delegated: Boolean = false, deadline: Long? = null, result: ((result: AddressResponse?) -> Unit)) =
        getAddressGrpc(getRequestAddress(address, height,delegated), deadline, result)


    fun getAddress(address: String, height: Long? = null, delegated: Boolean = false, deadline: Long? = null): AddressRaw? {
        getAddressGrpc(address, height,delegated, deadline)?.let {
            return convertAddress.get(it, address)
        } ?: run {
            return null
        }
    }

    fun getAddress(address: String, height: Long? = null, delegated: Boolean = false, deadline: Long? = null, result: ((AddressRaw?) -> Unit)) {
        getAddressGrpc(address, height,delegated, deadline) {
            it?.let { result(convertAddress.get(it, address)) } ?: run { result(null) }
        }
    }

}