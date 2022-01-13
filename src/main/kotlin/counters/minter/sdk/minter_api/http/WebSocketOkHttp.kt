package counters.minter.sdk.minter_api.http

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.concurrent.TimeUnit

class WebSocketOkHttp(httpOptions: HttpOptions) {

    private val logger = KotlinLogging.logger {}
    private var nodeUrl: String
    private val httpOptions: HttpOptions
    private var headers: Map<String, String>?


    init {
        this.httpOptions = httpOptions
/*
        httpOptions.timeout?.let {
            builder.connectTimeout(it, TimeUnit.MILLISECONDS)
                .readTimeout(it, TimeUnit.MILLISECONDS)
        }*/
        headers = httpOptions.headers
        nodeUrl = httpOptions.raw!!
    }

    class State {
        lateinit var initialiser: () -> WebSocket
        val maxNumReconnect = 5  // Maximum number of reconnections
        val intervalReconnect = 5000L  // Reconnection interval, milliseconds
        lateinit var client: OkHttpClient
        lateinit var webSocket: WebSocket
        var isConnect = false
        var connectNum = 0
/*        class Socket(private val webSocket: WebSocket){
            fun send(text: String) = webSocket.send(text)
            fun cancel() = webSocket.cancel()
            fun close(){
                webSocket.cancel()
                webSocket.close( 1001 , "closes the connection" )
            }
        }
        val socket: Socket? = null*/
    }

    fun close(webSocket: WebSocket?) {
//        webSocket?.cancel()
        logger.warn { "webSocket?.close(1001, \"closes the connection\")" }
        webSocket?.close(1001, "closes the connection")
    }

    private fun reconnect(state: State) {
        logger.warn { "reconnect ${state.connectNum}" }
        if (state.connectNum <= state.maxNumReconnect) {
            try {
                runBlocking { delay(state.intervalReconnect) }
                state.initialiser.invoke()
                state.connectNum++
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } else {
            logger.warn { "reconnect over ${state.maxNumReconnect}" }
//            Log.i(TAG, "reconnect over $MAX_NUM,please check url or network")
        }
    }

    fun socket(
        patch: String,
        params: List<Pair<String, String>>? = null,
        timeout: Long? = null,
        function: (result: String) -> Unit
    ): WebSocket {
        val clientBuilder = OkHttpClient.Builder()
        timeout?.let {
            clientBuilder.writeTimeout(it, TimeUnit.MILLISECONDS)
            clientBuilder.readTimeout(it, TimeUnit.MILLISECONDS)
            clientBuilder.connectTimeout(it, TimeUnit.MILLISECONDS)
        }
        val client = clientBuilder.build()
        val state = State()
        state.client = client

        val requestBuilder = Request.Builder()
        headers?.forEach { requestBuilder.addHeader(it.key, it.value) }
        val httpUrl = (this.nodeUrl + "/" + patch).toHttpUrl()
        val httpBuilder = httpUrl.newBuilder()
        params?.forEach { httpBuilder.addQueryParameter(it.first, it.second) }
        requestBuilder.url(httpBuilder.build())
        logger.debug { "request: ${requestBuilder.build()}" }

        val request = requestBuilder.build()
        state.initialiser = {
            client.newWebSocket(request, object : WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    state.isConnect = false
                    logger.warn { "onClosed()" }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    state.isConnect = false
                    logger.warn { "onClosing()" }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    state.isConnect = false
                    logger.warn { "onFailure()" }
//                    reconnect(state)
//                    close(state.webSocket)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    function(text)
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    state.webSocket = webSocket
                    state.isConnect = response.code == 101
                    if (!state.isConnect) {
//                        reconnect(state)
                    } else {
                        logger.debug { "connect success" }
                    }

                }
            }
            )
        }
        return state.initialiser()
    }

}