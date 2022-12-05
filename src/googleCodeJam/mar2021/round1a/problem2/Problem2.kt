package googleCodeJam.mar2021.round1a.problem2

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.math.pow
import kotlin.system.exitProcess

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src/googleCodeJam/mar2021/round1a/problem2/testInput.txt"))
val input = Scanner(File("src/googleCodeJam/mar2021/round1a/problem2/customInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(nextLine())
    for (caseNumber in 1..totalCases) {
        val linesToRead = Integer.parseInt(nextLine())
        val cards = mutableListOf<Int>()
        for (i in 0 until linesToRead) {
            val splitLine = nextLine().split(" ").map { Integer.parseInt(it) }
            cards.addAll(List(splitLine[1]) { splitLine[0] })
        }
        val totalCombinations = 2.0.pow(cards.size).toInt()
        var maxScore = 0
        for (i in 1 until totalCombinations) {
            var binaryString = Integer.toBinaryString(i)
            binaryString = "0".repeat(cards.size - binaryString.length) + binaryString
            val indexedBinary = binaryString.map { it == '1' }.withIndex()

            val sumIndexes = indexedBinary.filter { it.value }.map { it.index }
            val productIndexes = indexedBinary.filterNot { it.value }.map { it.index }

            val sum = cards.slice(sumIndexes).sum()
            var product = 1
            for (j in cards.slice(productIndexes)) {
                product *= j
                if (product > sum) {
                    break
                }
            }
            if (product == sum) {
                maxScore = sum
            }
        }

        println("Case #$caseNumber: $maxScore")
    }
}

/**
 * Reads the next line from [input]. Terminates if a line reading 'end' is read. Prints lines beginning with '-----' and
 * then skips them
 * @return the next line from [input]
 */
private fun nextLine(): String {
    var line: String
    while (true) {
        line = input.nextLine()
        if (line.startsWith("-----")) {
            println(line)
            continue
        }
        if (line == "end") {
            println("End of inputs, exiting")
            exitProcess(0)
        }
        break
    }
    return line
}
