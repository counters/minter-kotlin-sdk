package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.FrozenResponse
import counters.minter.grpc.client.candidateKeyOrNull
import counters.minter.grpc.client.moveToCandidateKeyOrNull
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.FrozenAllRaw

class ConvertFrozenAll : MinterMatch() {

    fun get(response: FrozenResponse): List<FrozenAllRaw> {
        val array = ArrayList<FrozenAllRaw>()

        response.frozenList.forEach {
            val symbol = it.coin.symbol
            val coinId = it.coin.id
            val coin = CoinObjClass.CoinObj(
                id = coinId,
                symbol = symbol
            )
            val item = FrozenAllRaw(
                height = it.height,
                address = it.address,
                candidateKey = it.candidateKeyOrNull?.value,
                coin = coin,
                value = getAmount(it.value),
                move_to_candidate_key = it.moveToCandidateKeyOrNull?.value,
            )
            array.add(item)
        }
        return array
    }
}
