package googleCodeJam.mar2021.qualifying.problem1

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
val input = Scanner(File("src\\googleCodeJam\\mar2021\\qualifying\\problem1\\testInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        val listSize = Integer.parseInt(input.nextLine())
        var list = input.nextLine().split(" ").map { Integer.parseInt(it) }
        check(list.isNotEmpty()) { "Empty list" }
        check(list.size == listSize) { "Unexpected size" }

        var cost = 0
        // Skip the last iteration, list will be sorted
        for (i in 0 until list.size - 1) {
            // Locate minimum between i and end of list inclusive
            // Can use index of as all items in list are distinct
            val minPos = list.indexOf(list.subList(i, list.size).min()!!)

            // Reverse the sublist from i to minPos inclusive (no need to reverse a list of length 1)
            if (minPos != i) {
                list = list.subList(0, i)
                        .plus(list.subList(i, minPos + 1).reversed())
                        .plus(list.subList(minPos + 1, list.size))
            }

            //Calculate cost (size of list that was reversed)
            // +1 because inclusive, and yes, they count reversing a singleton as cost 1...
            cost += minPos - i + 1
        }
        println("Case #$caseNumber: $cost")
    }
}