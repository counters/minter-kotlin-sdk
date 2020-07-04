import MinterApi.MinterApi

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello from Kotlin")

            val minterApi = MinterApi("http://127.0.0.1:8841", 30.0)

            val events = minterApi.getEventsRaw(37920)
            println(events)

            val status = minterApi.getStatus()
            println(status) // Status(height=977154, datetime=2019-07-11T21:26:22.119+03:00, network=minter-mainnet-1)
              val node = minterApi.getNodeRaw("Mp3b6e2632cd0c91ef96ea4f4a16f554ff1b4dd41324fd421a0161947c50603b9b")
            println(node) // NodeRaw(reward=Mx0a76...dd711, owner=Mx5ebe...7799, pub_key=Mp01cc...5b2c, commission=10, crblock=4)
            val coin = minterApi.getCoin("ROBOT")
            println(coin) //  Coin(id=null, symbol=ROBOT, length=5, name=Coin for robots, creater=null, crr=80, ...)
            val wallet = minterApi.getAddress("Mxabcd4613b06bc5a78412cb55a09bdf3f94790321")
            println(wallet) // Wallet(id=null, address=Mxabcd4613b06bc5a78412cb55a09bdf3f94790321, count_txs=17, balance={UPLOAD=5.3534213964374E-5, BIP=4245.51470327139, SATOSHI=888.8888})
            val block = minterApi.getBlockRaw(1532991)
            println(block)
            val transaction = minterApi.getTransactionRaw("Mt083bdf87f22ccfe62d55951eaa6b7a8d7618f83f214df6b0e516858bc3a837ef")
            println(transaction)

            val estimateCoinSell = minterApi.estimateCoinSell("ROBOT", 1.0, "BIP")
            println(estimateCoinSell) // EstimateCoinSell(willGet=21.908222878076707, commission=0.1)
            val estimateCoinBuy = minterApi.estimateCoinBuy("BIP", 1.0, "UPLOAD")
            println(estimateCoinBuy) // EstimateCoinBuy(willPay=0.04564504450213553, commission=0.1)

            val newNode = minterApi.getNode("Mp3b6e2632cd0c91ef96ea4f4a16f554ff1b4dd41324fd421a0161947c50603b9b", 0, {
                val reward = minterApi.getAddress(it)
//                println(reward)
                0
            }, {
                val owner = minterApi.getAddress(it)
//                println(owner)
                if (owner != null) {
                    owner.balance!!.forEach { coin, amount ->
                        if (coin != "BIP") {
                            val priceInBip = minterApi.estimateCoinSell(coin, amount, "BIP")!!.willGet
                            println("$amount $coin = $priceInBip BIP") // 100.00006004578127 UPLOAD = 4.564748939706455 BIP
                        } else {
                            println("$amount $coin")
                        }
                    }
                }
                0
            })

        }
    }

}
