package googleCodeJam.mar2021.round1a.problem1

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.system.exitProcess

val generatedInput = File("src/googleCodeJam/mar2021/round1a/problem1/generatedInput.txt")
val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src/googleCodeJam/mar2021/round1a/problem1/testInput.txt"))
//val input = Scanner(File("src/googleCodeJam/mar2021/round1a/problem1/customInput.txt"))
//val input = Scanner(generatedInput)

/**
 * The number of digits that have been appended to the current string. Should be reset for each test case
 */
private var appendedDigits = 0

fun main() {
//    generateInput(5)
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        // Allow for some breaking lines in the input for readability of my test files
        // Allow for an 'end' statement for my test files
        var line: String
        while (true) {
            line = input.nextLine()
            if (line.startsWith("---")) {
                println("---------")
                continue
            }
            if (line == "end") {
                exitProcess(0)
            }
            break
        }

        val expectedListSize = Integer.parseInt(line)
        val list = input.nextLine().split(" ").map { NumericalString(it) }.toMutableList()
        check(list.size == expectedListSize) { "Unexpected size" }
        check(list.isNotEmpty()) { "No items" }
        appendedDigits = 0

        for (i in 1 until list.size) {
            val previousItem = list[i - 1]
            val currentItem = list[i]
            if (currentItem > previousItem) {
                continue
            }
            if (currentItem == previousItem) {
                currentItem.appendDigits("0")
                continue
            }
            // previousItem.size() is always bigger else (currentItem > previousItem) above would have been true
            val lengthDifference = previousItem.size() - currentItem.size()
            if (lengthDifference == 0) {
                currentItem.appendDigits("0")
                continue
            }

            // Try adding all zeros to make a minimal, same-length string
            // This will work if for example previous is 15 and current is 3, result will be 30
            var testItem = currentItem.plusDigits("0".repeat(lengthDifference))
            if (testItem > previousItem) {
                list[i] = testItem
                appendedDigits += lengthDifference
                continue
            }
            // Try copying the remaining digits of previous, rather than adding zeros
            // If they're identical, we can increment previous and use that
            //     e.g. if previous is 15 and current is 1, this results in 16
            testItem = currentItem.plusDigits(previousItem.value.takeLast(lengthDifference))
            if (testItem == previousItem) {
                testItem.increment()
                // Ensure adding 1 doesn't change the starting digits (e.g. 19 -> 20)
                if (testItem.value.take(currentItem.size()) == currentItem.value) {
                    list[i] = testItem
                    appendedDigits += lengthDifference
                    continue
                }
            }
            currentItem.appendDigits("0".repeat(lengthDifference + 1))
        }

        println("Case #$caseNumber: $appendedDigits")
    }
}

/**
 * A string which represents an arbitrarily-sized numerical value
 */
private class NumericalString(value: String) : Comparable<NumericalString> {
    var value = value
        private set

    init {
        check(value.isNumeric()) { "Contains non-numerical characters" }
    }

    fun size() = value.length

    override fun compareTo(other: NumericalString): Int {
        val compareLengths = value.length.compareTo(other.value.length)
        if (compareLengths != 0) return compareLengths

        var i = 0
        val chunkSize = 8
        while (i < value.length) {
            val end = (i + chunkSize).coerceAtMost(value.length)
            val xInt = value.substring(i, end).toInt()
            val yInt = other.value.substring(i, end).toInt()

            val compare = xInt.compareTo(yInt)
            if (compare != 0) return compare

            i += chunkSize
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NumericalString) return super.equals(other)
        return compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * Edits the current [value], appending the string with the given digits. Increments [appendedDigits] accordingly
     */
    fun appendDigits(appendDigits: String) {
        check(appendDigits.isNumeric()) { "Contains non-numerical characters" }
        value += appendDigits
        appendedDigits += appendDigits.length
    }

    /**
     * @return the result of appending [appendedDigits] to [value]
     */
    fun plusDigits(appendDigits: String): NumericalString {
        check(appendDigits.isNumeric()) { "Contains non-numerical characters" }
        return NumericalString(value + appendDigits)
    }

    /**
     * Increases the numerical value of [value] by 1
     */
    fun increment() {
        // If can just increment last number
        if (value.last() != '9') {
            value = value.substring(0, value.length - 1) + (value.last().toString().toInt() + 1)
            return
        }
        val lastNonNineIndex = value.indexOfLast { it != '9' }
        // If all 9s
        if (lastNonNineIndex == -1) {
            value = "1" + "0".repeat(value.length)
            return
        }
        // Ends in string of 9s, replace them all with 0s and increment the next digit
        value = (value.substring(0, lastNonNineIndex)
                + (value[lastNonNineIndex].toString().toInt() + 1)
                + "0".repeat(value.length - lastNonNineIndex - 1)
        )
    }
}

/**
 * @return whether the string contains only numbers
 */
private fun String.isNumeric() = this.matches(Regex("[0-9]+"))

/**
 * Helper function for generating random data to test with
 */
@Suppress("unused")
private fun generateInput(size: Int) {
    val stringBuilder = StringBuilder(size.toString())
    stringBuilder.append("\n")
    for (i in 0 until size) {
        val listSize = 100
        stringBuilder.append(listSize)
        stringBuilder.append("\n")
        stringBuilder.append(List(listSize) { (Random().nextFloat() * 10.0.pow(9).roundToLong()) }.joinToString(" "))
        stringBuilder.append("\n")
    }
    generatedInput.writeText(stringBuilder.toString())
}
