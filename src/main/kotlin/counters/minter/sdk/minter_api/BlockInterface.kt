package counters.minter.sdk.minter_api

import counters.minter.grpc.client.ApiServiceGrpc
import counters.minter.grpc.client.BlockField
import counters.minter.grpc.client.BlockRequest
import counters.minter.grpc.client.BlockResponse
import counters.minter.sdk.minter.MinterRaw
import counters.minter.sdk.minter_api.convert.ConvertBlock
import java.util.concurrent.TimeUnit

sealed interface BlockInterface  {
    var asyncClient: ApiServiceGrpc.ApiServiceStub
    var blockingClient: ApiServiceGrpc.ApiServiceBlockingStub

    val convertBlock: ConvertBlock

    fun block(height: Long, deadline: Long? = null): MinterRaw.BlockRaw? {
            blockGrpc(height, deadline)?.let {
                return convertBlock.get(it)
            } ?: run {
                return null
            }
    }

    fun blockGrpc(height: Long, deadline: Long? = null): BlockResponse? {
        val request = BlockRequest.newBuilder().setHeight(height).build()
        return blockGrpc(request, deadline)
    }

    fun blockGrpc(request: BlockRequest, deadline: Long? = null): BlockResponse? {
            val blockingClient = if (deadline != null) blockingClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else blockingClient
            blockingClient.block(request)?.let {
                return it
            } ?: run {
                return null
            }
    }

    /**
     * fields=transactions&fields=missed&fields=block_reward&fields=size&fields=proposer&fields=validators&fields=evidence
     * &failed_txs=true
     * */
    fun asyncBlockGrpc(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: BlockResponse?) -> Unit)) {
        val requestBuilder = BlockRequest.newBuilder().setHeight(height)
        fields?.let {
            it.forEach { requestBuilder.addFields(it) }
            if (!it.contains(BlockField.block_reward)) requestBuilder.addFields(BlockField.block_reward)
        }
        failed_txs?.let { requestBuilder.setFailedTxs(it) }
        val request = requestBuilder.build()
        asyncBlockGrpc(request, deadline, result)
    }

    fun asyncBlockGrpc(request: BlockRequest, deadline: Long? = null, result: ((result: BlockResponse?) -> Unit)) {
        var success = false
        val asyncClient = if (deadline != null) asyncClient.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS) else asyncClient
        asyncClient.block(request, ResponseStreamObserver(request, {
            if (!success) result(null)
        }) {
            success = true
            result(it)
        })
    }

    fun asyncBlock(height: Long, fields: List<BlockField>?=null, failed_txs: Boolean?=null, deadline: Long? = null, result: ((result: MinterRaw.BlockRaw?) -> Unit)) {
        asyncBlockGrpc(height, fields, failed_txs, deadline) {
            it?.let {
                result(convertBlock.get(it))
            } ?: run {
                result(null)
            }
        }
    }

}