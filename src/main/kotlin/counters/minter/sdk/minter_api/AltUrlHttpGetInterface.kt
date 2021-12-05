package counters.minter.sdk.minter_api

interface AltUrlHttpGetInterface {
    fun altUrlHttpGet(params: List<Pair<String, String>>): String {
        if (params.isEmpty()) return ""
        val array = arrayListOf<String>()
        params.forEach { array.add("${it.first}=${it.second}") }
        return "?" + array.joinToString("&")
    }
}
