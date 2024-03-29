## Minter Blockchain Kotlin SDK (unofficial)

### About
Minter Blockchain Kotlin SDK [Minter.network](https://minter.network).

![Minter Blockchain Kotlin SDK](static/minter-kotlin-sdk-header.png "Minter Blockchain Kotlin SDK")


| Class support    | MinterApi  | MinterAsyncHttpApi, MinterCoroutinesHttpApi | MinterApiCoroutines, MinterGrpcApi  |
| ------- | :----- | ----- | :----- |
| HTTP transport      | :heavy_check_mark: | :heavy_check_mark:  | :heavy_multiplication_x: |
| gRPC transport      | :heavy_check_mark: | :heavy_multiplication_x: | :heavy_check_mark: |
| Kotlin data class in output | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
| JSON in output | :heavy_check_mark: | :heavy_check_mark: | :heavy_multiplication_x: |
| gRPC class in output         | :heavy_multiplication_x: | :heavy_multiplication_x: | :heavy_check_mark: |

## Using a published package
[Instruction](https://docs.github.com/ru/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

## pom

```xml
<dependency>
  <groupId>counters</groupId>
  <artifactId>minter-kotlin-sdk</artifactId>
  <version>2.5.1</version>
</dependency> 
```

## Kotlin DSL

```kotlin
dependencies {
    implementation("counters:minter-kotlin-sdk")
//    implementation("counters:minter-kotlin-sdk:2.5.1")
    // options
    implementation("io.grpc:grpc-netty-shaded:1.50.2")
}
```

## Gradle Groovy

```groovy
dependencies {
    implementation 'counters:minter-kotlin-sdk'
}
```

```shell
./gradlew installDist
# or
mvn install
```

#### Initializing the HTTP Api
```kotlin
val minterApi = MinterApi(HttpOptions(raw = "http://localhost:8843/v2"))
```
#### Initializing gRPC transport
```kotlin
val minterApi2 = MinterApiCoroutines(GrpcOptions(hostname = "localhost", deadline = 1000))
```

#### Request with HTTP
```kotlin
minterApi.getStatus().let { println(it) } // Status(height=977154, datetime=2019-07-11T21:26:22.119+03:00, network=minter-mainnet-1)
```

#### Request with gRPC
```kotlin
minterApi2.getStatus().let { println(it) } // Status(height=7345972, datetime=2021-11-13T16:15:49.410+03:00, network=minter-mainnet-4)
```

#### Request with gRPC and return gRPC object
```kotlin
minterApi2.getStatusGrpc().let { println(it) } // latest_block_hash: "2201A179A3A69............
```

#### Asynchronous request with gRPC
```kotlin
minterApi2.asyncStatus { println(it) } // Status(height=7345972, datetime=2021-11-13T16:15:49.410+03:00, network=minter-mainnet-4)
```

#### Asynchronous request with gRPC and return gRPC object
```kotlin
minterApi2.asyncStatusGrpc { println(it) } // latest_block_hash: "2201A179A3A69............
```

### Details on HTTP methods

#### Node (getCandidate)
Returns candidate’s info by provided public_key.
```kotlin
val node = minterApi.getNodeRaw("Mp3b6e2632cd0c91ef96ea4f4a16f554ff1b4dd41324fd421a0161947c50603b9b")
println(node) // NodeRaw(reward=Mx0a76...dd711, owner=Mx5ebe...7799, pub_key=Mp01cc...5b2c, commission=10, crblock=4)
```

#### Coin (getCoinInfo)
Returns information about coin.
```kotlin
val coin = minterApi.getCoin("ROBOT")
println(coin) //  Coin(id=null, symbol=ROBOT, length=5, name=Coin for robots, creater=null, crr=80, ...)
```

#### Wallet (getBalance)
Returns coins list, balance and transaction count (for nonce) of an address.
```kotlin
val wallet = minterApi.getAddress("Mxabcd4613b06bc5a78412cb55a09bdf3f94790321")
println(wallet) // Wallet(id=null, address=Mxcd633fd8ec1b0a181627dfd72f9ba25e93f0c899, count_txs=17, balance={UPLOAD=5.3534213964374E-5, BIP=4245.51470327139, SATOSHI=888.8888})       
```

#### Block (getBlock)
Returns block data at given height.
```kotlin
val block = minterApi.getBlockRaw(2)
println(block) /* BlockRaw(height=2, time=2019-05-15T18:05:02.395+03:00, num_txs=3, total_txs=3, reward=333.0, size=4310, proposer=Mp...,
              transaction=[TransactionRaw(hash=Mt..., height=2, type=5, from=Mx..., to=null, node=, stake=15000000000000000000000000,
                coin=UPLOAD, amount=1.5E7, gas_price=1, commission=null, payload=false, gas=100000, gascoin=BIP)},
                "payload":"","gas":"10000","from":"Mx...","service_data":"","gas_coin":"BIP","type":6,"raw_tx":"******","nonce":"1",
                "hash":"Mt.....","tags":{"tx.type":"06","tx.from":"****"}}])
                */          
```

#### Transaction (getTransactionRaw)
Returns transaction.
```kotlin
val transaction = minterApi.getTransactionRaw("Mt0e8fa325a4541c628448cdb53dd02455cfe80e01e848920ba5836bb67105ee21")
            println(transaction) /* TransactionRaw(hash=0E..21, height=1733080, type=1, from=Mx0903..af1b, to=Mxabcd..0321, node=null, 
            stake=40960000000000000000, coin=UPLOAD, coin2=null, amount=40.96, gas_price=1, commission=null, payload=true, gas=134, gascoin=BIP)
            */          
```

#### Events (getEventsRaw)
Returns event.
```kotlin
val events = minterApi.getEventsRaw(37920)
println(events) /*  [EventRaw(height=37920, node=Mp8888..8888, wallet=Mxd4b4..a80d, coin=null, type=Reward, amount=5.4633626394576E-4, role=Delegator),...]
            */          
```

#### estimateCoinSell (estimateCoinSell)
Return estimate of sell coin transaction
```kotlin
val estimateCoinSell = minterApi.estimateCoinSell("ROBOT", 1.0, "BIP")
println(estimateCoinSell) // EstimateCoinSell(willGet=21.908222878076707, commission=0.1)
          
```

#### estimateCoinBuy (estimateCoinBuy)
Return estimate of buy coin transaction.
```kotlin
val estimateCoinBuy = minterApi.estimateCoinBuy("BIP", 1.0, "UPLOAD")
println(estimateCoinBuy) // EstimateCoinBuy(willPay=0.04564504450213553, commission=0.1)    
```

#### transactions?query Example
```kotlin
val queryMap = mutableMapOf(
    QueryTags.TagsTxTo to "72b9ccf56eb6e1a0d00b50dd92314109649033be",
    QueryTags.TagsTxType to TransactionTypes.TypeSend.toHex(),
)
minterApi.getTransactionsRaw(queryMap, 1, 100)?.let { println(it) }
```


#### Advanced Use Example
```kotlin
val newNode = minterApi.getNode("Mp01cc99ae5a349ecaeef187dcbb12816bf2b3d8eae80f654034b21213aa445b2c", 0, {
    val reward = minterApi.getAddress(it)
//  println(reward)
    0
    }, {
        val owner = minterApi.getAddress(it)
        if (owner != null) {
            owner.balance.forEach { coin, amount ->
                if (coin != "BIP") {
                    val priceInBip = minterApi.estimateCoinSell(coin, amount, "BIP")!!.willGet
                    println ("$amount $coin = $priceInBip BIP") // 100.00006004578127 UPLOAD = 4.564748939706455 BIP
                } else {
                    println ("$amount $coin")
                }
            }
        }
    0
    }
)
```



