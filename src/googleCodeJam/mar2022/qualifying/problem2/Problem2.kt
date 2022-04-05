package googleCodeJam.mar2022.qualifying.problem2

import java.util.*

const val INK_REQUIRED = 1_000_000

fun main() {
    val input = Scanner(System.`in`)
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
        val printers = List(3) {
            input.nextLine().split(" ").map { it.toInt() }
        }
        val minValues = MutableList(4) { index ->
            min(printers.map { it[index] })
        }

        if (minValues.sum() < INK_REQUIRED ) {
            printCase(testCaseIndex, null)
            continue
        }

        val output = MutableList(4) { 0 }
        var remainingInkNeeded = INK_REQUIRED
        while (remainingInkNeeded > 0) {
            val min = min(minValues.filter { it > 0 })
            val minIndex = minValues.indexOf(min)

            if (min >= remainingInkNeeded) {
                output[minIndex] = remainingInkNeeded
                remainingInkNeeded = 0
                break
            }
            output[minIndex] = min
            minValues[minIndex] = -1
            remainingInkNeeded -= min
        }

        check(remainingInkNeeded == 0) { "Uh oh" }
        printCase(testCaseIndex, output.joinToString(" "))
    }
}

fun printCase(caseNumber: Int, successString: String?) =
    println("Case #$caseNumber: " + (successString.takeIf { !it.isNullOrBlank() } ?: "IMPOSSIBLE"))

/**
 * Custom min function because apparently Google doesn't understand that Kotlin has a built in function for this
 */
fun min(list: List<Int>): Int {
    var min = Int.MAX_VALUE
    list.forEach {
        if (it < min) {
            min = it
        }
    }
    return min
}