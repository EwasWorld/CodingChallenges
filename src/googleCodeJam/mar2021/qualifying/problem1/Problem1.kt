package googleCodeJam.mar2021.qualifying.problem1

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*


/*
Musings:

4213
4[213]
position 3
reverse 1-3
1243
*/

val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src\\googleCodeJam\\mar2021\\qualifying\\problem1\\testInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        val listSize = Integer.parseInt(input.nextLine())
        var list = input.nextLine().split(" ").map { Integer.parseInt(it) }
        check(list.size == listSize) { "UnexpectedSize" }

        var cost = 0
        // Skip the last iteration, list will be sorted
        for (i in 0 until list.size - 1) {
            /*
             * Locate minimum between i and end of list inclusive
             */
            var minPos = i
            var minVal = list[i]
            for (j in (i + 1) until list.size) {
                if (list[j] < minVal) {
                    minPos = j
                    minVal = list[j]
                }
            }

            /*
             * Reverse the sublist from i to minPos inclusive
             */
            if (minPos != i) {
                list = list.subList(0, i)
                        .plus(list.subList(i, minPos + 1).reversed())
                        .plus(list.subList(minPos + 1, list.size))
            }

            /*
             * Calculate cost
             */
            // Cost is size of list that was reversed
            cost += minPos - i + 1 // +1 because inclusive, and yes, they count reversing a singleton as cost 1...
        }
        println("Case #$caseNumber: $cost")
    }
}