package googleCodeJam.mar2021.round1b.problem2

import java.io.File
import java.util.*
import kotlin.system.exitProcess

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
val input = Scanner(File("src/googleCodeJam/mar2021/round1b/problem2/testInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(nextLine())
    for (caseNumber in 1..totalCases) {
        val problemParams = nextLine().split(" ").map { Integer.parseInt(it) }
        val n = problemParams[0]
        val a = problemParams[1]
        val b = problemParams[2]
        val requiredMetals = nextLine().split(" ").map { Integer.parseInt(it) }
        check(requiredMetals.size == n) { "Invalid input size" }
        check(a < b) { "Invalid input parameters" }

        var possible = true
        if (a % 2 == 0 && b % 2 == 0 && !(requiredMetals.all { it % 2 == 0 } || requiredMetals.all { it % 2 == 0 })) {
            possible = false
        }

        if (possible) {
            /*
             * Create all max items in min size
             */
            val trialSize = requiredMetals.size
            val metals = Metals(a, b, mutableListOf(trialSize))

        }
        println("Case #$caseNumber: ")
    }
}

private class Metals(private val a: Int, private val b: Int, private val currentMetals: MutableList<Int>) {
    init {
        currentMetals.sort()
    }

    fun applySpell(index: Int) {
        if (currentMetals.size >= index) {
            throw IndexOutOfBoundsException()
        }
        val metalToSplit = currentMetals.removeAt(index)
        val newMetals = listOf(metalToSplit - a, metalToSplit - b).filter { it > 0 }
        currentMetals.addAll(newMetals)
        currentMetals.sort()
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
