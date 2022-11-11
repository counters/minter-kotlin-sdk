package counters.minter.sdk.minter_api.convert

import counters.minter.grpc.client.CandidateResponse
import counters.minter.sdk.minter.CoinObjClass
import counters.minter.sdk.minter.MinterMatch
import counters.minter.sdk.minter.models.Candidate
import counters.minter.sdk.minter.enums.CandidateStatus
import counters.minter.sdk.minter.models.Stake

class ConvertCandidate : MinterMatch() {

    fun get(response: CandidateResponse): Candidate? {
        val stakes = ArrayList<Stake>()

        response.stakesList.forEach { stake->
            stakes.add( Stake(
                owner = stake.owner,
                coin = CoinObjClass.CoinObj(
                    id = stake.coin.id,
                    symbol = stake.coin.symbol
                ),
                value = getAmount(stake.value),
                bipValue = getAmount(stake.bipValue),
            ))
        }
        val status = CandidateStatus.byRaw(response.status.toInt())

        if (status != null)
            return Candidate(
                reward = response.rewardAddress,
                owner = response.ownerAddress,
                control = response.controlAddress,
                totalStake = getAmount(response.totalStake),
                publicKey = response.publicKey ,
                commission = response.commission.toInt(),
                slots = response.usedSlots.value.toInt(),
                users = response.uniqUsers.value.toInt(),
                minStake = getAmount(response.minStake.value),
                stakes = stakes,
                status = status,
                validator = response.validator,
                jailedUntil = response.jailedUntil
            )
        return null
    }
}
