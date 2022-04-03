package googleCodeJam.mar2022.qualifying.problem3

import java.util.*

fun main() {
    val input = Scanner(System.`in`)
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
        val totalDice = input.nextLine().toInt()
        val dice = input.nextLine().split(" ").map { it.toInt() }.sorted().toMutableList()
        check(dice.size == totalDice) { "Bad input" }

        var currentLength = 0
        dice.forEach {
            if (it >= currentLength + 1) {
                currentLength++
            }
        }

        println("Case #$testCaseIndex: $currentLength")
    }
}