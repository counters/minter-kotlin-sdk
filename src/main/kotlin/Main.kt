import counters.minter.sdk.minter_api.MinterHttpApiOld

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello from Kotlin")

            val minterHttpApiOld = MinterHttpApiOld("http://127.0.0.1:8843/v2", 30.0)

            minterHttpApiOld.getStatus()?.let { println(it) }

            minterHttpApiOld.getNodeRaw("Mpaaaaa16ebd6af229b4cfc02c3ab40bd25c1051c3aa2120f07d08c1bd01777777")?.let { println(it) }

            minterHttpApiOld.getAddress("Mxeee37fedf95e5ee65ce6e3ad1cbcfa9055932311", 0, true)?.let { println(it) }

            minterHttpApiOld.getBlockRaw(4000)?.let { println(it) }

//            minterApi.getEventsRaw(567, null, true).let{ println(it) }  // 120

            minterHttpApiOld.getTransactionRaw("Mt6a4825073e2df8ccecf0c0ec524b8b9bc528d8a9fcca75d63d7949061d338954")?.let { println(it) }

            minterHttpApiOld.getCoinRaw("ROBOT")?.let { println(it) } // 65
            minterHttpApiOld.getCoinRaw(65)?.let { println(it) } // 65

            minterHttpApiOld.estimateCoinSell("ROBOT", 1.0, "BIP")?.let { println(it) }
            minterHttpApiOld.estimateCoinSell(65, 1.0, 0)?.let { println(it) }

            minterHttpApiOld.estimateCoinBuy("BIP", 1.0, "UPLOAD")?.let { println(it) }
            minterHttpApiOld.estimateCoinBuy(0, 1.0, 272)?.let { println(it) }

            minterHttpApiOld.getMinGasPrice()?.let { println(it) }
            minterHttpApiOld.getMaxGasPrice()?.let { println(it) }

        }
    }

}
