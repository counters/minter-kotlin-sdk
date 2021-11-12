package counters.minter.sdk.MinterApi.http

data class HttpOptions(
    var raw: String?=null,
    val scheme: String= "http",
    val hostname: String= "127.0.0.1",
    val port: Int?= 8843,
    val path: String="v2",
){
    init {
      if (raw==null) {
          val addPort = if (port!=null) ":$port" else ""
          raw = "$scheme://$hostname$addPort/$path"
      }
    }
}
