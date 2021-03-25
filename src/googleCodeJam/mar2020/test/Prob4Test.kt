package googleCodeJam.mar2020.test

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.min

val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

/**
 * I used this file to work out how to get the interactive problem's local test tool working
 * Spoilers, I have no idea what I'm doing and it's not working...
 */
fun main(args: Array<String>) {
    var testCaseCount = 3
    var size = 100

    if (input.hasNextLine()) {
        val line = input.nextLine().trim().split(" ").map { it.toInt() }
        testCaseCount = line[0]
        size = line[1]
    }

    QualifyingProblem4(testCaseCount, size).run()
}

class QualifyingProblem4(private val testCaseCount: Int, private val size: Int) {
    private lateinit var db: Problem4StdOutDatabase
    private lateinit var currentState: MutableList<Boolean?>

    // Index of the next null in #currentState. Note that for mirror pairs, if one of them is null, it will always
    //   be the one at the end of the array (i.e. could have a state of [0,0,null,0] but not [0,null,0,0])
    private var nextNullFromStart = -1
    private var nextNullFromEnd = -1

    // Whether the next bit to query is the next null from the start (false: should check next from end)
    private var nextNullIsStart = true

    // As explained in the musings at the top, these represent those two bits min bits that must be checked (index
    //   of the bit that was 0/1 before the fluctuation)
    private var zeroCheckBit = -1
    private var oneCheckBit = -1

    // If the two bits that are the same are 0s
    private var isThreeZeros = true


    fun run() {
        for (testCaseNumber in 0 until testCaseCount) {
            /*
             * Reset the state
             */
            db = Problem4StdOutDatabase()
            currentState = arrayOfNulls<Boolean?>(size).toMutableList()

            nextNullFromStart = 0
            nextNullFromEnd = size - 1
            nextNullIsStart = true

            zeroCheckBit = -1
            oneCheckBit = -1
            isThreeZeros = true

            /*
             * Initial 10 queries are free - the first fluctuation happens before the first item is given so can be ignored
             */
            for (i in 0 until min(10, size)) {
                readNextBit(false)
            }

            while (currentState.contains(null)) {
                // Should have worked out the database by now
                check(db.queries < 150) { "Too many queries. Null count: " + currentState.count { it == null } }
//            println("Null count: " + currentState.count { it == null })

                if (db.queries % 10 != 0) {
                    readNextBit(true)
                }
                /*
                 * Handle the fluctuation that comes next
                 */
                else {
                    /*
                     * Find a mirror pair of 01 11 as described in musings at the top
                     * Unless the pairings were previously found
                     */
                    if (zeroCheckBit == -1) {
                        var mirrorPairSame: Int? = null
                        var mirrorPairDiff: Int? = null
                        /*
                         * Find the two pairs
                         */
                        for (i in 0 until nextNullFromStart) {
                            val mirror = size - i - 1
                            if (currentState[mirror] == null || (mirrorPairSame != null && mirrorPairDiff != null)) {
                                break
                            }

                            if (mirrorPairSame == null && currentState[i] == currentState[mirror]) {
                                mirrorPairSame = i
                            }
                            else if (mirrorPairDiff == null && currentState[i] != currentState[mirror]) {
                                mirrorPairDiff = i
                            }
                        }

                        /*
                         * Ensure there's one same and one different mirror pair found
                         */
                        if (mirrorPairSame == null || mirrorPairDiff == null) {
                            // Can just do a complement because if all pairs are
                            //   Same: a reverse does nothing
                            //   Different: a reverse does the same as a complement, and both does the same as nothing
                            // See musings at top for examples
                            // NOTE1: Possibility of failing if there is a mirror pair with one value and one null
                            val currentFirstItem = currentState[0]!!
                            if (currentFirstItem != db.getBit(0)) {
                                currentState = currentState.map { it?.not() }.toMutableList()
                            }
                            continue
                        }

                        /*
                         * Ensure the indexes are of the DIFFERENT side of the mirror (the indexes of 10 rather than 11)
                         */
                        if (currentState[mirrorPairDiff] == currentState[mirrorPairSame]) {
                            mirrorPairSame = size - mirrorPairSame - 1
                            mirrorPairDiff = size - mirrorPairDiff - 1
                        }

                        /*
                     * Populate required variables
                     */
                        isThreeZeros = currentState[mirrorPairSame] == false
                        if (isThreeZeros) {
                            zeroCheckBit = mirrorPairSame
                            oneCheckBit = mirrorPairDiff
                        }
                        else {
                            zeroCheckBit = mirrorPairDiff
                            oneCheckBit = mirrorPairSame
                        }
                    }

                    /*
                     * Calculate the change that happened
                     */
                    val zeroBitValue = db.getBit(zeroCheckBit)
                    val oneBitValue = db.getBit(oneCheckBit)
                    var reverse = false
                    var complement = false

                    // The two different bits have become the two same bits, therefore is a reverse
                    if (zeroBitValue == oneBitValue) {
                        reverse = true

                        // If originally there were 3 zeros and now the bit is a true (or 3 ones and is now false),
                        if (isThreeZeros && zeroBitValue || !isThreeZeros && !zeroBitValue) {
                            complement = true
                        }
                    }
                    // Not a reverse, and the zero bit has become a 1, so just a complement
                    else if (zeroBitValue) {
                        complement = true
                    }

                    /*
                     * Apply the change and keep zeroCheckBit and oneCheckBit correct
                     */
                    if (reverse) {
                        currentState.reverse()
                        zeroCheckBit = size - zeroCheckBit - 1
                        oneCheckBit = size - oneCheckBit - 1

                        // If deduced database contains a mirrored pair that is a value and a null, they swap places on
                        //   a reverse, thus changing the indexes of the next null item
                        if (size - 1 - nextNullFromStart != nextNullFromEnd) {
                            nextNullFromStart = currentState.indexOfFirst { it == null }
                            nextNullFromEnd = currentState.indexOfLast { it == null }
                        }
                    }
                    if (complement) {
                        currentState = currentState.map { it?.not() }.toMutableList()
                        val temp = zeroCheckBit
                        zeroCheckBit = oneCheckBit
                        oneCheckBit = temp
                        isThreeZeros = !isThreeZeros
                    }
                }
            }

            val result = db.isDatabaseCorrect(currentState.filterNotNull())
            if (!result) {
                break
            }
        }
    }


    /**
     * Read the next unknown bit
     * @param preventMirror whether this query be checking to prevent a mirrored pair of a value and a null
     */
    private fun readNextBit(preventMirror: Boolean) {
        if (nextNullIsStart) {
            // Prevent a mirror pair of a value and a null if the next query will cause a fluctuation
            // See NOTE1 for reason
            if (preventMirror && zeroCheckBit == -1 && db.queries % 10 == 9) {
                db.getBit(0)
                return
            }
            currentState[nextNullFromStart] = db.getBit(nextNullFromStart)
            nextNullFromStart++
        }
        else {
            currentState[nextNullFromEnd] = db.getBit(nextNullFromEnd)
            nextNullFromEnd--
        }
        // Could finish this test case and check the db if (nextNullFromStart == nextNullFromEnd) but I think a while
        //   condition is more readable
        nextNullIsStart = !nextNullIsStart
    }


    open class Problem4StdOutDatabase() {
        var queries = 0
            protected set

        fun getBit(index: Int): Boolean {
            check(index >= 0) { "Query is bad and you should feel bad" }
            check(queries <= 150) { "No more queries allowed" }

            queries++
            return transact((index + 1).toString())
        }

        fun isDatabaseCorrect(guess: List<Boolean>): Boolean {
            return transact(databaseToString(guess))
        }

        private fun transact(output: String): Boolean {
            println(output)
            check(input.hasNextLine()) { "No reply received from server" }
            val line = input.nextLine().trim()
            return line == "Y" || line == "1"
        }

        private fun databaseToString(list: List<Boolean?>): String {
            return list.joinToString("") {
                when (it) {
                    null -> "-"
                    true -> "1"
                    false -> "0"
                }
            }
        }
    }
}