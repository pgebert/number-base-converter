package converter

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

class Exit : Exception()
class Back : Exception()

const val PRECISION = 5

/**
 * Main: entry point and command flow loop
 *
 */
fun main() {

    while (true) {
        try {

            val (baseSource, baseTarget) = promptBase()

            while (true) {
                try {
                    val number = promptNumber(baseSource, baseTarget)
                    val isFractional = number.contains(".")

                    val numberInDecimal = convertToDecimal(number, baseSource)
                    val result = convertFromDecimal(numberInDecimal, baseTarget, isFractional)

                    println("Conversion result: $result")

                } catch (e: Back) {
                    break
                }
            }
        } catch (e: Exit) {
            return
        } catch (e: Exception) {
            println(e.message)
        }
    }

}

/**
 * Prompt user for source and target base
 *
 * @return pair of source and target base
 */
fun promptBase(): Pair<Int, Int> {
    println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
    val input = readln()

    if (input == "/exit") throw Exit()

    val bases = input.split(" ").map { it.toInt() }

    return Pair(bases.first(), bases.last())

}

/**
 * Prompt number to convert from one number system to another
 *
 * @param baseSource base of the source number system
 * @param baseTarget base of the target number system
 * @return number to convert
 */
fun promptNumber(baseSource: Int, baseTarget: Int): String {
    println("Enter number in base $baseSource to convert to base $baseTarget (To go back type /back)")
    val input = readln()

    if (input == "/back") throw Back()

    return input
}

/**
 * Convert a number from the decimal system to a different base
 *
 * @param number number to convert
 * @param base base of the target number system
 * @param isFractional whether the number is a fraction
 * @return the number in the target number system
 */
fun convertFromDecimal(number: BigDecimal, base: Int, isFractional: Boolean = true): String {

    val digits = (CharRange('0', '9') + CharRange('a', 'z')).toList()

    val nonFractionalPart = mutableListOf<Int>()
    var quotient = number.toBigInteger()
    val base = base.toBigInteger()

    while (quotient > BigInteger.ZERO) {
        nonFractionalPart.add(0, (quotient % base).toInt())
        quotient /= base
    }

    val fractionalPart = mutableListOf<Int>()
    var remainder = number.remainder(BigDecimal.ONE)

    while (fractionalPart.size < PRECISION) {
        remainder = remainder.remainder(BigDecimal.ONE) * base.toBigDecimal()
        fractionalPart.add(remainder.toInt())
    }

    val nonFractionalPartString = nonFractionalPart.map { digits[it] }.joinToString("").ifEmpty { "0" }
    val fractionalPartString = fractionalPart.map { digits[it] }.joinToString("").ifEmpty { "0" }

    val result = when (isFractional) {
        true -> "$nonFractionalPartString.$fractionalPartString"
        else -> nonFractionalPartString
    }
    return result
}

/**
 * Convert a number from a different number system to the decimal system
 *
 * @param number number to convert
 * @param base base of the source system
 * @return number in the decimal system
 */
fun convertToDecimal(number: String, base: Int): BigDecimal {


    val digits = (CharRange('0', '9') + CharRange('a', 'z')).toList()
    var result = BigDecimal.ZERO

    var power = number.split(".").first().length - 1

    number.replace(".", "").forEach { character ->
        result += digits.indexOf(character).toBigDecimal() * base.toDouble().pow(power).toBigDecimal()
        power--
    }

    return result
}