package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.sdk.minter_api.convert.Convert
import counters.minter.sdk.minter_api.convert.ConvertSwapFrom
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import mu.KotlinLogging

class MinterGrpcApi(grpcOptions: GrpcOptions):
    TransactionInterface,
    TransactionsInterface,
    StatusInterface,
    BlockInterface,
    SubscribeInterface,
    UnconfirmedTxs,
    LimitOrdersOfPoolInterface,
    LimitOrdersInterface,
    LimitOrderInterface,
    EventInterface,
    AddressInterface,
    EstimateCoinSellInterface,
    EstimateCoinSellAllInterface,
    EstimateCoinBuyInterface,
    SwapPoolInterface,
    CoinInfoInterface
{
    private var channel: ManagedChannel? = null
    private var grpcOptions: GrpcOptions? = null
    override lateinit var asyncClient: ApiServiceGrpc.ApiServiceStub
    override lateinit var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    override val convertSwapFrom = ConvertSwapFrom()
    private val convert = Convert()

    override val convertStatus = convert.status
    override val convertTransaction = convert.transaction
    override val convertBlock = convert.block
    override val convertSubscribe = convert.subscribe
    override val convertLimitOrder = convert.limitOrder
    override val convertEvents = convert.events
    override val convertAddress = convert.address
    override val convertEstimateCoinSell = convert.estimateCoinSell
    override val convertEstimateCoinSellAll = convert.estimateCoinSellAll
    override val convertEstimateCoinBuy = convert.estimateCoinBuy
    override val convertSwapPool = convert.convertSwapPool
    override val convertCoinInfo = convert.convertCoinInfo


    override val logger = KotlinLogging.logger {}

    var exception: Boolean = true
        set(value) {
            field = value
            convert.transaction.exception = value
            convert.block.exception = value
            convertEvents.exception = value
        }

    init {
        convert.transaction.exception = exception
        convert.block.exception = exception
        convertEvents.exception = exception
            this.grpcOptions = grpcOptions

        val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
            .maxInboundMessageSize(9999999)
        if (grpcOptions.ssl_contest != null) {
//                channelBuilder.useTransportSecurity(grpcOptions.ssl_contest)
//                channelBuilder.sslContext(grpcOptions.ssl_contest)
        } else if (grpcOptions.useTransportSecurity) {
            channelBuilder.useTransportSecurity()
        } else {
            channelBuilder.usePlaintext()
        }
        channel = channelBuilder.build()
        blockingClient = ApiServiceGrpc.newBlockingStub(channel)
        asyncClient = ApiServiceGrpc.newStub(channel)
    }

    fun shutdown(): Unit {
        channel?.shutdown()
    }


}