package counters.minter.sdk.MinterApi.convert

class Convert: ConvertStatusOld, ConvertTransactionOld {
    val status = ConvertStatus
    val transaction = ConvertTransaction()
    val block = ConvertBlock()


}