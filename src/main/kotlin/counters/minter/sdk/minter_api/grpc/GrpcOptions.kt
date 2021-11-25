package counters.minter.sdk.minter_api.grpc

import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext

data class GrpcOptions(
    val hostname: String="127.0.0.1",
    val port: Int=8842,
    /** in Millisecond */
    val deadline: Long? = null,
    val useTransportSecurity: Boolean = false,
    val ssl_contest: SslContext? = null,
//    val credentials: GoogleCredentials,

)
