package counters.minter.sdk.minter_api

interface AltUrlHttpGetInterface {
    fun altUrlHttpGet(params: List<Pair<String, String>>): String {
        var addPathForURL = ""
        val array = arrayListOf<String>()
        params.forEach { array.add("${it.first}=${it.second}") }
        if(array.count()>0) addPathForURL = "?" + array.joinToString("&")
        return addPathForURL
    }
}
