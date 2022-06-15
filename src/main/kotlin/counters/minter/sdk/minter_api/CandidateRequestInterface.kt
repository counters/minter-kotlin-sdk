package counters.minter.sdk.minter_api

import counters.minter.grpc.client.CandidateRequest

interface CandidateRequestInterface {

    fun getRequestCandidate(public_key: String, not_show_stakes: Boolean? = null, height: Long? = null): CandidateRequest {
        val requestBuilder = CandidateRequest.newBuilder()
        not_show_stakes?.let { requestBuilder.setNotShowStakes(it) }
        height?.let { requestBuilder.setHeight(it) }
        return requestBuilder.setPublicKey(public_key).build()
    }

}
