package googleCodeJam.mar2021.qualifying.problem3

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src/googleCodeJam/mar2021/qualifying/problem3/testInput.txt"))
val input = Scanner(File("src/googleCodeJam/mar2021/qualifying/problem3/customInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        val line = input.nextLine().split(" ").map { Integer.parseInt(it) }
        val n = line[0]
        val goalCost = line[1]
        var out: List<Int> = listOf()

        val maxCost = ((n * (1 + n)) / 2) - 1
        val minCost = n - 1 // Every iteration does at least one reverse, last iteration is skipped
        if (goalCost in minCost..maxCost) {
            // Start with an ascending list
            out = IntArray(n) {it + 1}.toMutableList()
            var currentCost = 0
            for (i in (n - 2) downTo 0) {
                val maxCostIncrease = n - i
                var swapPosition = n - 1 // Start with max swap
                if (currentCost + i + 1 >= goalCost) {
                    break
                }
                else if (currentCost + i + maxCostIncrease > goalCost) {
                    swapPosition = goalCost - currentCost - 1
                }

                out = out.subList(0, i)
                        .plus(out.subList(i, swapPosition + 1).reversed())
                        .plus(out.subList(swapPosition + 1, out.size))
                currentCost += swapPosition - i + 1
            }
        }

        println("Case #$caseNumber: " + if (out.isNullOrEmpty()) "IMPOSSIBLE" else out.joinToString(" "))
    }
}

fun main2() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        val line = input.nextLine().split(" ").map { Integer.parseInt(it) }
        val n = line[0]
        val goalCost = line[1]
        var out: List<Int> = listOf()

        val maxCost = ((n * (1 + n)) / 2) - 1
        val minCost = n - 1 // Every iteration does at least one reverse, last iteration is skipped
        if (goalCost in minCost..maxCost) {
            var swapsToDo = 0 // Do this many swaps
            var index = n - 1 //         at this index
            var currentCost = 0
            for (i in 0 until n - 1) {
                val maxSwaps = n - i
                val remainingMandatorySwaps = n - i - 1 - 1
                if (currentCost + remainingMandatorySwaps + maxSwaps >= goalCost) {
                    index = i
                    swapsToDo = goalCost - currentCost - remainingMandatorySwaps
                    break
                }
                currentCost += n - i
            }

            // Start with an ascending list
            out = IntArray(n) {it + 1}.toMutableList()
            out = out.subList(0, index)
                    .plus(out.subList(index, index + swapsToDo).reversed())
                    .plus(out.subList(index + swapsToDo, out.size))
            for (i in index - 1 downTo 0) {
                out = out.subList(0, i)
                        .plus(out.subList(i, out.size).reversed())
            }
        }

        println("$n\n" + if (out.isNullOrEmpty()) "IMPOSSIBLE" else out.joinToString(" "))
//        println("Case #$caseNumber: " + if (out.isNullOrEmpty()) "IMPOSSIBLE" else out.joinToString(" "))
    }
}
