package counters.minter.sdk.minter_api.convert

class Convert: ConvertStatusOld, ConvertTransactionOld {
    val status = ConvertStatus
    val transaction = ConvertTransaction()
    val block = ConvertBlock()
    val subscribe = ConvertSubscribe


}