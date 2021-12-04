package counters.minter.sdk.minter

import java.math.BigDecimal
import java.math.RoundingMode


open class MinterMatch {

//    private val FEE_BASE_STRING = "0.001"
//    val FEE_BASE = BigDecimal(FEE_BASE_STRING)

    //    private val PIP = 10 ** -18;
    private val PIP_STR = "0.000000000000000001"
    private val PIP = BigDecimal(PIP_STR)
    //    private val UNIT = 10 ** -15;
    private val UNIT_STR = "0.000000000000001"
    private val UNIT = BigDecimal(UNIT_STR)

//    private val parseBlock = P
/*
    public const PIP = 10 ** -18;
    public const PIP_STR = '0.000000000000000001';
    public const UNIT = 10 ** -15;
    public const UNIT_STR = '0.000000000000001';*/


    fun getAmount(pip: String): Double {
        val numF = BigDecimal(pip).multiply(PIP)
//        println(numF)
        return numF.toDouble()
    }

    fun getPip(amount: Double): String {
//        println(BigDecimal(amount).divide(PIP).toPlainString() )
        val numStr = BigDecimal(amount).divide(PIP).setScale(0, RoundingMode.HALF_UP)
//        return numStr.round(0)
        return numStr.toString()
//        return numStr.toPlainString()
    }
}
