package counters.minter.sdk.minter_api

import counters.minter.grpc.client.*
import counters.minter.sdk.minter.LimitOrderRaw
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter_api.convert.ConvertEvents
import io.grpc.StatusRuntimeException
import mu.KLogger
import java.util.concurrent.TimeUnit

interface EventInterface : EventsRequestInterface {

    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertEvents: ConvertEvents

    val logger: KLogger

    fun getEventsGrpc(request: EventsRequest, deadline: Long?): EventsResponse? {
        try {
            blockingClient.events(request)?.let {
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

    fun getEventsGrpc(height: Long, search: List<String>? = null, deadline: Long? = null) =
        getEventsGrpc(getRequestEvents(height, search), deadline)

    fun getEventsGrpc(request: EventsRequest, deadline: Long? = null, result: ((result: EventsResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.events(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            result(it)
            success = true
        })
    }

    fun getEventsGrpc(height: Long, search: List<String>? = null, deadline: Long? = null, result: ((result: EventsResponse?) -> Unit)) =
        getEventsGrpc(getRequestEvents(height, search), deadline, result)

    fun getEvents(height: Long, search: List<String>? = null, deadline: Long? = null): List<MinterRaw.EventRaw>? {
        getEventsGrpc(height, search, deadline)?.let {
            return convertEvents.get(it, height)
        } ?: run {
            return null
        }
    }

    fun getEvents(height: Long, search: List<String>? = null, deadline: Long? = null, result: ((List<MinterRaw.EventRaw>?) -> Unit)) {
        getEventsGrpc(height, search, deadline) {
            it?.let { result(convertEvents.get(it, height)) } ?: run { result(null) }
        }
    }

}