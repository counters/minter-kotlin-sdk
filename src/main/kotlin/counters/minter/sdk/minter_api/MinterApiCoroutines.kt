package counters.minter.sdk.minter_api

import com.google.protobuf.Empty
import counters.minter.grpc.client.*
import counters.minter.sdk.minter.*
import counters.minter.sdk.minter.Coin
import counters.minter.sdk.minter.enum.Subscribe
import counters.minter.sdk.minter.enum.SwapFromTypes
import counters.minter.sdk.minter.models.*
import counters.minter.sdk.minter_api.convert.Convert
import counters.minter.sdk.minter_api.convert.ConvertBestTradeType
import counters.minter.sdk.minter_api.convert.ConvertSwapFrom
import counters.minter.sdk.minter_api.grpc.GrpcOptions
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

class MinterApiCoroutines(grpcOptions: GrpcOptions? = null) :
    LimitOrderRequestInterface,
    LimitOrdersRequestInterface,
    LimitOrdersOfPoolRequestInterface,
    EventsRequestInterface,
    AddressRequestInterface,
    EstimateCoinSellRequestInterface,
    EstimateCoinSellAllRequestInterface,
    EstimateCoinBuyRequestInterface,
    SubscribeRequestInterface,
    SwapPoolRequestInterface,
    BestTradeRequestInterface,
    CoinInfoRequestInterface,
    CandidateRequestInterface,
    FrozenAllRequestInterface,
    SwapPoolProviderRequestInterface {

    //    private var callOptions: CallOptions = CallOptions.DEFAULT
    private lateinit var stub: ApiServiceGrpcKt.ApiServiceCoroutineStub
    private var channel: ManagedChannel? = null
    private var grpcOptions: GrpcOptions

    private val minterMatch = MinterMatch()

    private val convert = Convert()
    private val logger = KotlinLogging.logger {}
    private val requestEmpty = Empty.newBuilder().build()

    private val convertLimitOrder = convert.limitOrder

    private val convertEvents = convert.events
    private val convertAddress = convert.address
    private val convertEstimateCoinSell = convert.estimateCoinSell
    private val convertEstimateCoinSellAll = convert.estimateCoinSellAll
    private val convertEstimateCoinBuy = convert.estimateCoinBuy
    private val convertSubscribe = convert.subscribe
    private val convertSwapPool = convert.convertSwapPool
    private val convertBestTrade = convert.convertBestTrade
    private val convertCoinInfo = convert.convertCoinInfo
    private val convertFrozenAll = convert.convertFrozenAll

    override val convertSwapFrom = ConvertSwapFrom()

    private val convertBestTradeType = ConvertBestTradeType()

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

        this.grpcOptions = grpcOptions ?: GrpcOptions()
        initClient()
    }

    private fun initClient() {
        val channelBuilder = ManagedChannelBuilder.forAddress(grpcOptions.hostname, grpcOptions.port)
        channelBuilder.maxInboundMessageSize(9999999)//9999999
        if (grpcOptions.ssl_contest != null) {

        } else if (grpcOptions.useTransportSecurity) {
            channelBuilder.useTransportSecurity()
        } else {
            channelBuilder.usePlaintext()
        }
        channel = channelBuilder.build()

        if (grpcOptions.deadline != null) stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)
//            .withDeadlineAfter(grpcOptions.deadline!!, TimeUnit.MILLISECONDS) // TODO(Global Deadline?!)
        else stub = ApiServiceGrpcKt.ApiServiceCoroutineStub(channel!!)
    }

    fun shutdown(): Unit {
        channel?.shutdown()
    }

    suspend fun getStatusGrpc(deadline: Long? = null): StatusResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else
            this.stub
        return try {
            stub.status(requestEmpty)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }

    }

    suspend fun getStatus(deadline: Long? = null): Minter.Status? {
        getStatusGrpc(deadline)?.let {
            return convert.status.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getTransactionGrpc(request: TransactionRequest, deadline: Long? = null): TransactionResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.transaction(request)
        } catch (e: StatusException) {
            logger.error { "StatusException: $e" }
            null
        }
    }

    suspend fun getTransactionGrpc(hash: String, deadline: Long? = null): TransactionResponse? {
        val request = TransactionRequest.newBuilder().setHash(hash).build()
        return getTransactionGrpc(request, deadline)
    }

    suspend fun getTransaction(hash: String, deadline: Long? = null): TransactionRaw? {
        getTransactionGrpc(hash, deadline)?.let {
            return convert.transaction.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getBlockGrpc(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): BlockResponse? {
        val requestBuilder = BlockRequest.newBuilder().setHeight(height)
        fields?.let {
            it.forEach { requestBuilder.addFields(it) }
            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
        failed_txs?.let { requestBuilder.setFailedTxs(it) }
        val request = requestBuilder.build()
        return getBlockGrpc(request, deadline)
    }

    suspend fun getBlockGrpc(request: BlockRequest, deadline: Long? = null): BlockResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.block(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getBlock(height: Long, fields: List<BlockField>? = null, failed_txs: Boolean? = null, deadline: Long? = null): MinterRaw.BlockRaw? {
        getBlockGrpc(height, fields, failed_txs, deadline)?.let {
            return convert.block.get(it)
        } ?: run {
            return null
        }
    }

    suspend fun getLimitOrderGrpc(request: LimitOrderRequest, deadline: Long? = null): LimitOrderResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrder(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrderGrpc(orderId: Long, height: Long? = null, deadline: Long? = null) = getLimitOrderGrpc(getRequestLimitOrder(orderId, height), deadline)

    suspend fun getLimitOrder(orderId: Long, height: Long? = null, deadline: Long? = null): LimitOrderRaw? {
        getLimitOrderGrpc(orderId, height, deadline).let {
            it?.let { return convertLimitOrder.get(it) } ?: run { return null }
        }
    }

    suspend fun getLimitOrdersGrpc(request: LimitOrdersRequest, deadline: Long? = null): LimitOrdersResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrders(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrdersGrpc(ids: List<Long>, height: Long? = null, deadline: Long? = null) = getLimitOrdersGrpc(getRequestLimitOrders(ids, height), deadline)

    suspend fun getLimitOrders(ids: List<Long>, height: Long?, deadline: Long?): List<LimitOrderRaw>? {
        getLimitOrdersGrpc(ids, height, deadline).let {
            it?.let { return convertLimitOrder.getList(it.ordersList) } ?: run { return null }
        }
    }

    suspend fun getLimitOrdersOfPoolGrpc(request: LimitOrdersOfPoolRequest, deadline: Long? = null): LimitOrdersOfPoolResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.limitOrdersOfPool(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getLimitOrdersOfPoolGrpc(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null) =
        getLimitOrdersOfPoolGrpc(getRequestLimitOrdersOfPool(sellCoin, buyCoin, limit, height), deadline)

    suspend fun getLimitOrdersOfPool(sellCoin: Long, buyCoin: Long, limit: Int? = null, height: Long? = null, deadline: Long? = null): List<LimitOrderRaw>? {
        getLimitOrdersOfPoolGrpc(sellCoin, buyCoin, limit, height, deadline).let {
            it?.let { return convertLimitOrder.getList(it.ordersList) } ?: run { return null }
        }
    }


    suspend fun getEventsGrpc(request: EventsRequest, deadline: Long? = null): EventsResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.events(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getEventsGrpc(height: Long, search: List<String>? = null, deadline: Long? = null) =
        getEventsGrpc(getRequestEvents(height, search), deadline)

    suspend fun getEvents(height: Long, search: List<String>? = null, deadline: Long? = null): List<MinterRaw.EventRaw>? {
        getEventsGrpc(height, search, deadline).let {
            it?.let { return convertEvents.get(it, height) } ?: run { return null }
        }
    }

    suspend fun getAddressGrpc(request: AddressRequest, deadline: Long? = null): AddressResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.address(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getAddressGrpc(address: String, height: Long? = null, delegated: Boolean? = null, deadline: Long? = null) =
        getAddressGrpc(getRequestAddress(address, height, delegated), deadline)

    suspend fun getAddress(address: String, height: Long? = null, delegated: Boolean? = null, deadline: Long? = null): AddressRaw? {
        getAddressGrpc(address, height, delegated, deadline).let {
            it?.let { return convertAddress.get(it, address) } ?: run { return null }
        }
    }

    suspend fun estimateCoinSellGrpc(request: EstimateCoinSellRequest, deadline: Long? = null): EstimateCoinSellResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.estimateCoinSell(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun estimateCoinSellGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ) = estimateCoinSellGrpc(getRequestEstimateCoinSell(coinToSell, valueToSell, coinToBuy, height, coin_id_commission, swap_from, route), deadline)

    suspend fun estimateCoinSell(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ): Coin.EstimateCoin? {
        estimateCoinSellGrpc(coinToSell, minterMatch.getPip(valueToSell), coinToBuy, height, coin_id_commission, swap_from, route, deadline).let {
            it?.let { return convertEstimateCoinSell.get(it) } ?: run { return null }
        }
    }


    suspend fun estimateCoinSellAllGrpc(request: EstimateCoinSellAllRequest, deadline: Long? = null): EstimateCoinSellAllResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.estimateCoinSellAll(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun estimateCoinSellAllGrpc(
        coinToSell: Long,
        valueToSell: String,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ) = estimateCoinSellAllGrpc(getRequestEstimateCoinSellAll(coinToSell, valueToSell, coinToBuy, height, gas_price, swap_from, route), deadline)


    suspend fun estimateCoinSellAll(
        coinToSell: Long,
        valueToSell: Double,
        coinToBuy: Long = 0,
        height: Long? = null,
        gas_price: Int? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ): Coin.EstimateCoin? {
        estimateCoinSellAllGrpc(coinToSell, minterMatch.getPip(valueToSell), coinToBuy, height, gas_price, swap_from, route, deadline).let {
            it?.let { return convertEstimateCoinSellAll.get(it) } ?: run { return null }
        }
    }


    suspend fun estimateCoinBuyGrpc(request: EstimateCoinBuyRequest, deadline: Long? = null): EstimateCoinBuyResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.estimateCoinBuy(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun estimateCoinBuyGrpc(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ) = estimateCoinBuyGrpc(getRequestEstimateCoinBuy(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route), deadline)


    suspend fun estimateCoinBuy(
        coinToBuy: Long,
        valueToBuy: String,
        coinToSell: Long = 0,
        height: Long? = null,
        coin_id_commission: Long? = null,
        swap_from: SwapFromTypes? = null,
        route: List<Long>? = null,
        deadline: Long? = null
    ): Coin.EstimateCoin? {
        estimateCoinBuyGrpc(coinToBuy, valueToBuy, coinToSell, height, coin_id_commission, swap_from, route, deadline).let {
            it?.let { return convertEstimateCoinBuy.get(it) } ?: run { return null }
        }
    }

    fun streamSubscribeGrpc(request: SubscribeRequest, deadline: Long? = null): Flow<SubscribeResponse?> {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.subscribe(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            flowOf<SubscribeResponse?>(null)
        }
    }

    fun streamSubscribeGrpc(query: String, deadline: Long? = null) = streamSubscribeGrpc(getRequestSubscribe(query), deadline)
    fun streamSubscribeGrpc(query: Subscribe, deadline: Long? = null) = streamSubscribeGrpc(getRequestSubscribe(query.str), deadline)

    fun streamSubscribeStatus(deadline: Long? = null) = flow {
        streamSubscribeGrpc(Subscribe.TmEventNewBlock, deadline).collect {
            it?.let { emit(convertSubscribe.status(it)) } ?: run { emit(null) }
        }
    }

    suspend fun getSwapPoolGrpc(request: SwapPoolRequest, deadline: Long? = null): SwapPoolResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.swapPool(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getSwapPoolGrpc(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null) =
        getSwapPoolGrpc(getRequestSwapPool(coin0, coin1, height), deadline)

    suspend fun getSwapPool(coin0: Long, coin1: Long, height: Long? = null, deadline: Long? = null): MinterRaw.SwapPoolRaw? {
        getSwapPoolGrpc(coin0, coin1, height, deadline).let {
            it?.let { return convertSwapPool.get(it) } ?: run { return null }
        }
    }

    suspend fun getBestTradeGrpc(request: BestTradeRequest, deadline: Long? = null): BestTradeResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.bestTrade(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getBestTradeGrpc(sellCoin: Long, buyCoin: Long, amount: String, type: BestTradeRequest.Type, maxDepth: Int? = null, height: Long? = null, deadline: Long? = null) =
        getBestTradeGrpc(getRequestBestTrade(sellCoin, buyCoin, amount, type, maxDepth, height), deadline)

    suspend fun getBestTradeGrpc(sellCoin: Long, buyCoin: Long, amount: Double, type: BestTradeType, maxDepth: Int? = null, height: Long? = null, deadline: Long? = null) =
        getBestTradeGrpc(getRequestBestTrade(sellCoin, buyCoin, minterMatch.getPip(amount), convertBestTradeType.convBestTradeType(type), maxDepth, height), deadline)

    suspend fun getBestTrade(sellCoin: Long, buyCoin: Long, amount: Double, type: BestTradeType, maxDepth: Int? = null, height: Long? = null, deadline: Long? = null): BestTrade? {
        getBestTradeGrpc(sellCoin, buyCoin, amount, type, maxDepth, height, deadline).let {
            it?.let { return convertBestTrade.get(it) } ?: run { return null }
        }
    }

    suspend fun getCoinInfoGrpc(request: CoinIdRequest, deadline: Long? = null): CoinInfoResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.coinInfoById(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getCoinInfoGrpc(request: CoinInfoRequest, deadline: Long? = null): CoinInfoResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.coinInfo(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getCoinInfoGrpc(coin: Long, height: Long? = null, deadline: Long? = null) =
        getCoinInfoGrpc(getRequestCoinInfo(coin, height), deadline)

    suspend fun getCoinInfoGrpc(coin: String, height: Long? = null, deadline: Long? = null) =
        getCoinInfoGrpc(getRequestCoinInfo(coin, height), deadline)

    suspend fun getCoinInfo(coin: Long, height: Long? = null, deadline: Long? = null): MinterRaw.CoinRaw? {
        return getCoinInfoGrpc(coin, height, deadline)?.let {
            convertCoinInfo.get(it)
//            it?.let { return convertCoinInfo.get(it) } ?: run { return null }
        }
    }

    suspend fun getCoinInfo(coin: String, height: Long? = null, deadline: Long? = null): MinterRaw.CoinRaw? {
        return getCoinInfoGrpc(coin, height, deadline)?.let {
            convertCoinInfo.get(it)
//            it?.let { return convertCoinInfo.get(it) } ?: run { return null }
        }
    }

    suspend fun getCandidateGrpc(request: CandidateRequest, deadline: Long? = null): CandidateResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.candidate(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getCandidateGrpc(public_key: String, not_show_stakes: Boolean? = null, height: Long? = null, deadline: Long? = null) =
        getCandidateGrpc(getRequestCandidate(public_key, not_show_stakes, height), deadline)

    suspend fun getCandidate(public_key: String, not_show_stakes: Boolean? = null, height: Long? = null, deadline: Long? = null) {
        TODO("Not yet implemented")
    }

    suspend fun getFrozenAllGrpc(request: FrozenAllRequest, deadline: Long? = null): FrozenResponse? {
        val stub = if (deadline != null) this.stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else this.stub
        return try {
            stub.frozenAll(request)
        } catch (e: StatusException) {
            logger.warn { "StatusException: $e" }
            null
        }
    }

    suspend fun getFrozenAllGrpc(startHeight: Long? = null, endHeight: Long, addresses: List<String>? = null, coinIds: List<Long>? = null, height: Long? = null, deadline: Long? = null) =
        getFrozenAllGrpc(getRequestFrozenAll(startHeight, endHeight, addresses, coinIds, height), deadline)
    suspend fun getFrozenAll(startHeight: Long? = null, endHeight: Long, addresses: List<String>? = null, coinIds: List<Long>? = null, height: Long? = null, deadline: Long? = null): List<FrozenAllRaw>? {
        getFrozenAllGrpc(startHeight, endHeight, addresses, coinIds, height, deadline).let {
            it?.let { return convertFrozenAll.get(it) } ?: run { return null }
        }
    }

}
