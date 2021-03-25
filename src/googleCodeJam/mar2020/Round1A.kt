package googleCodeJam.mar2020

import java.util.*


fun main() {
    round1AProblem1(arrayOf())
}

/**
 * Round 1A Problem 1 - Pattern Matching:
 *
 * A pattern can only contain upper case English letters and *. A * can match to 0 or more upper case letters. Each * in 
 *   a string with more than one * can represent a different set of letters. All patterns contain at least 1 *
 *
 * Input: the number of patterns followed by each pattern on a new line
 * Output: a word that matches all patterns
 * Example:
 *   Input: 1\n4\n1 2 3 4\n2 1 4 3\n3 4 1 2\n4 3 2 1
 *   Output: Case #1: 4 0 0
 */
fun round1AProblem1(args: Array<String>) {
    val input = Scanner(
        "8\n5\n*CONUTS\n*COCONUTS\n*OCONUTS\n*CONUTS\n*S\n2\n*XZ\n*XYZ\n4\nH*O\nHELLO*\n*HELLO\nHE*\n2\nCO*DE\nJ*AM\n2\nCODE*\n*JAM\n2\nA*C*E\n*B*D*\n2\nA*C*E\n*B*D\n2\n**Q**\n*A*"
    )
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()
    val asterisk = '*'

    for (completedCases in 1..testCases) {
        var patternsLeft = input.nextLine().toInt() - 1
        var isPossible = true

        // The current pattern that matches all previous strings
        val currentPattern = input.nextLine().trim().split(asterisk)
        var currentPatternBefore = currentPattern[0]
        var currentPatternAfter = currentPattern[1]
        while (patternsLeft > 0 && isPossible) {
            val nextLine = input.nextLine().trim().split(asterisk)
            val nextLineBefore = nextLine[0]
            val nextLineAfter = nextLine[1]
            patternsLeft--

            if (currentPatternBefore.length >= nextLineBefore.length && currentPatternBefore.startsWith(nextLineBefore)) {
                // Do nothing
            }
            else if (currentPatternBefore.length < nextLineBefore.length && nextLineBefore.startsWith(currentPatternBefore)) {
                currentPatternBefore = nextLineBefore
            }
            else {
                isPossible = false
                break
            }
            if (currentPatternAfter.length >= nextLineAfter.length && currentPatternAfter.endsWith(nextLineAfter)) {
                // Do nothing
            }
            else if (currentPatternAfter.length < nextLineAfter.length && nextLineAfter.endsWith(currentPatternAfter)) {
                currentPatternAfter = nextLineAfter
            }
            else {
                isPossible = false
                break
            }
        }

        println("Case #$completedCases: " + if (isPossible) currentPatternBefore + currentPatternAfter else "*")
    }
}