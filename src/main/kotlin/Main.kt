import MinterApi.MinterApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello from Kotlin")

            val minterApi = MinterApi("https://minter-node-2.testnet.minter.network:8841")

            /*  val status = minterApi.getStatus()
              println(status)
              val node = minterApi.getNodeRaw("Mp01cc99ae5a349ecaeef187dcbb12816bf2b3d8eae80f654034b21213aa445b2c")
              println(node)
              val coin = minterApi.getCoin("BTCSECURE")
              println(coin)*/
            val block = minterApi.getBlockRaw(2)
            println(block)

            GlobalScope.launch {
                delay(3000)
                println("Hello from Kotlin Coroutines!")

            }
        }
    }
}
