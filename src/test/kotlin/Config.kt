import counters.minter.sdk.Utils
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import counters.minter.sdk.minter_api.http.HttpOptions

class Config {
    companion object {
//        private val hostname = "node2.knife.io"
                private val hostname = "node-api.testnet.minter.network"
//        private val hostname = "node-api.taconet.minter.network"
//        private val hostname = "localhost"
//        val httpOptions = HttpOptions(raw = "http://${hostname}:8843/v2"/*, timeout = 60000*/)
        val httpOptions = HttpOptions(raw = "https://node-api.testnet.minter.network/v2")
//        val grpcOptions = GrpcOptions(hostname = hostname/*, deadline = 30000*/)
        val grpcOptions = GrpcOptions(hostname = hostname, port = 28842/*, deadline = 30000*/)

//        val network = Utils.Network.Mainnet5
        val network = Utils.Network.TestNet
//        val network = Utils.Network.Taconet13

        val exception = true
//const val exception = false
    }
}