package googleCodeJam.mar2022.qualifying.problem1

import java.util.*

fun main() {
    val input = Scanner(System.`in`)
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
        val (rows, columns) = input.nextLine().split(" ").map { it.toInt() }.let { it[0] to it[1] }

        println("Case #$testCaseIndex:")
        for (row in 0..(rows * 2)) {
            val main = if (row % 2 == 0) "-" else "."
            val delim = if (row % 2 == 0) "+" else "|"
            var rowOutput = delim + List(columns) { main }.joinToString(delim) + delim
            if (row == 0 || row == 1) {
                rowOutput = rowOutput.replaceRange(0, 2, "..")
            }
            println(rowOutput)
        }
    }
}
