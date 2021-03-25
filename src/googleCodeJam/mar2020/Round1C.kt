package googleCodeJam.mar2020

import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    round1cProblem2(arrayOf())
//    round1cProblem1(arrayOf())
}

fun round1cProblem1(args: Array<String>) {
    val input = Scanner(
        "5\n4 4 SSSS\n3 0 SNSS\n2 10 NSNNSN\n0 1 S\n2 7 SSSSSSSS"
    )
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()

    // Only a number of test cases was given, nothing else
    if (!input.hasNextLine()) {
        return
    }

    for (completedCases in 1..testCases) {
        var isPossible = false
        val line = input.nextLine().split(" ")
        val startX = line[0].toInt()
        val startY = line[1].toInt()
        val path = line[2].split("")
        var mins = 0

        val pathXMoves = path.filter { it.isNotEmpty() }.map { if (it == "E") 1 else (if (it == "W") -1 else 0) }
        val pathYMoves = path.filter { it.isNotEmpty() }.map { if (it == "N") 1 else (if (it == "S") -1 else 0) }


        for (i in pathXMoves.indices.plus(pathXMoves.size)) {
            val endX = startX + pathXMoves.subList(0, i).sum()
            val endY = startY + pathYMoves.subList(0, i).sum()
            if (abs(endX) + abs(endY) <= i) {
                isPossible = true
                mins = i
                break
            }
        }

        println("Case #$completedCases: " + if (isPossible) mins else "IMPOSSIBLE")
    }
}

class RandomNumbersCase(q: Int, rStr: String) {
    // The first digit of q (null if q was -1)
    val firstDigit: Int? = if (q != -1) Character.getNumericValue(q.toString().first()) else null
    // The first letter of the output
    val firstLetter = rStr.first()
    // Whether leading zeros have been cut
    val hadLeadingZeros = q.toString().length != rStr.length
}

val cases = mutableListOf<RandomNumbersCase>()
// ith set is all possible characters for the the digit i
var letterPossibilities: List<MutableSet<Char>> = listOf()
val lettersFound = mutableSetOf<Char>()

fun round1cProblem2(args: Array<String>) {
    val input = Scanner(File("src/puzzles/googleCodingJam2020/sample.in.txt"))
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()

    // Only a number of test cases was given, nothing else
    if (!input.hasNextLine()) {
        return
    }

    for (completedCases in 1..testCases) {
        val maxDigits = input.nextLine().toInt()
        var foundAns = false

        for (i in 0 until 10.0.pow(4).toInt()) {
            val line = input.nextLine().split(" ")
            val r = line[1]
            val newCase = RandomNumbersCase(-1, r)
//            val newCase = Problem2Case(line[0].toInt(), line[1])
            cases.add(newCase)
            // Collect letters until all 10 possible letters have been found
            if (lettersFound.size < 10) {
                lettersFound.addAll(r.map { it })
                if (lettersFound.size < 10) continue
                // First time it's been over 10: as soon as all letters are found

                letterPossibilities = List(10) { lettersFound.toMutableSet() }

                for (case in cases) {
                    foundAns = analyseCase(case)
                }
                if (foundAns) break
            }
            else {
                foundAns = analyseCase(newCase)
                if (foundAns) break
            }
        }

        // If it's still not solved, use the frequency analysis
        if (!foundAns) {
            val order = analyseFrequencies()
            for (letter in order) {
                for (poss in letterPossibilities) {
                    if (poss.size > 1 && poss.contains(letter)) {
                        poss.removeAll { it != letter }
                        checkLetters()
                    }
                }
            }
        }
        println("Case #$completedCases: " + letterPossibilities.map { it.first() }.joinToString(""))
    }
}

/**
 * Analyses the first letter and updates [letterPossibilities]
 * @return whether the letter mapping has been found
 */
fun analyseCase(case: RandomNumbersCase): Boolean {
    if (case.firstDigit != null && !case.hadLeadingZeros) {
        for (j in (case.firstDigit + 1)..9) {
            letterPossibilities[j].remove(case.firstLetter)
        }
    }
    letterPossibilities[0].remove(case.firstLetter)
    return checkLetters()
}

/**
 * Analyses [letterPossibilities] to remove clashes
 * @return whether the letter mapping has been found
 */
fun checkLetters(): Boolean {
    // Check if a letter appears in only one possibilities list
    for (letter in lettersFound) {
        val foundLetters = letterPossibilities.mapIndexed { i, it -> it.contains(letter) to i }.filter { it.first }.map { it.second }
        if (foundLetters.size == 1) {
            letterPossibilities[foundLetters[0]].removeAll { it != letter }
        }
    }
    // Check if a possibilities set has a single letter in it
    for (i in letterPossibilities.indices) {
        if (letterPossibilities[i].size == 1) {
            for (poss in letterPossibilities.indices) {
                if (poss != i) {
                    letterPossibilities[poss].remove(letterPossibilities[i].first())
                }
            }
        }
    }
    return letterPossibilities.all { it.size == 1 }
}

/**
 * Analyse the frequencies of each case's first letter
 * @return the letters ordered using their number of appearances
 */
fun analyseFrequencies(): List<Char> {
    val frequencies = mutableListOf<Pair<Int, Char>>()
    val firstLetters = cases.map {it.firstLetter}
    for (letter in lettersFound) {
        frequencies.add(firstLetters.count { it == letter } to letter)
    }
    frequencies.sortBy { it.first }
    // Move zero to the end (0 occurrences) then flip them so that ith element is most likely to be letter i
    val zero = frequencies[0]
    frequencies.removeAt(0)
    frequencies.add(zero)
    return frequencies.reversed().map {it.second}
}

/**
 * Helper function to check that [letterPossibilities] still allows the answer to the test problem
 */
fun isStillCorrect() {
    val ans = "TPFOXLUSHB"
    for (i in letterPossibilities.indices) {
        if (!letterPossibilities[i].contains(ans[i])) {
            println("Oh no!")
        }
    }
}

fun round1cProblem3(args: Array<String>) {
    val input = Scanner(
        "1\n-48 11"
    )
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()

    // Only a number of test cases was given, nothing else
    if (!input.hasNextLine()) {
        return
    }

    for (completedCases in 1..testCases) {

        println("Case #$completedCases: ")
    }
}