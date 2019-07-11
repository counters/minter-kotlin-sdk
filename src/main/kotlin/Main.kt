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

            /**/
            val status = minterApi.getStatus()
              println(status)
              val node = minterApi.getNodeRaw("Mp01cc99ae5a349ecaeef187dcbb12816bf2b3d8eae80f654034b21213aa445b2c")
              println(node)
              val coin = minterApi.getCoin("BTCSECURE")
            println(coin)
            val block = minterApi.getBlockRaw(973584)
            println(block)
            val estimateCoinSell = minterApi.estimateCoinSell("BIP", 1.0, "BIP")
            println(estimateCoinSell) // EstimateCoinSell(willGet=21.908222878076707, commission=0.1)
            val estimateCoinBuy = minterApi.estimateCoinBuy("BIP", 1.0, "BTCSECURE")
            println(estimateCoinBuy) // EstimateCoinBuy(willPay=0.04564504450213553, commission=0.1)

            val newNode = minterApi.getNode("Mp01cc99ae5a349ecaeef187dcbb12816bf2b3d8eae80f654034b21213aa445b2c", 0, {
                val reward = minterApi.getAddress(it)
//                println(reward)
                0
            }, {
                val owner = minterApi.getAddress(it)
//                println(owner)
                if (owner != null) {
                    owner.balance.forEach { coin, amount ->
                        if (coin != "BIP") {
                            val priceInBip = minterApi.estimateCoinSell(coin, amount, "BIP")!!.willGet
                            println("$amount $coin = $priceInBip BIP") // 100.00006004578127 BTCSECURE = 4.564748939706455 BIP
                        } else {
                            println("$amount $coin")
                        }
                    }
                }
                0
            })


            GlobalScope.launch {
                delay(3000)
                println("Hello from Kotlin Coroutines!")

            }
        }
    }
}
