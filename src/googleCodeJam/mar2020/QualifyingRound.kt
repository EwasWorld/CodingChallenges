package googleCodeJam.mar2020

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.abs
import kotlin.math.min


/**
 * Google's 2020 Code Jam - Qualifying Round
 * https://codingcompetitions.withgoogle.com/codejam/archive
 * My result:
 *   Qualified for next round with 42 points from problems 1, 2, and 3
 *   Problem 5 attempted but without success. I think I lacked the Maths for this one, so I just kind of brute-forced it
 *   Problem 4 attempted and completed after the competition ended (I doubt I would have made it in time if I had stayed
 *     up, given it took me a while to complete it and I didn't properly start attempting problems until the evening)
 *
 * All problems must take their input from standard in and send their output to standard out
 * For interactive problems, the created program must sends data to the judging system
 *   via standard out and receives replies back via standard in
 *
 * First line of the input is always the number of test cases
 * In cases where there is more than one answer, any correct answer will be accepted
 *
 * Each problem has a maximum time and space allowance for each test case (see the website for full details)
 */
fun main() {
    println("PROBLEM 1 =======================================")
    qualifyingProblem1(arrayOf())
    println("\n\nPROBLEM 2 =======================================")
    qualifyingProblem2(arrayOf())
    println("\n\nPROBLEM 3 =======================================")
    qualifyingProblem3(arrayOf())
    println("\n\nPROBLEM 4 =======================================")
    qualifyingProblem4(arrayOf())
    println("\n\nPROBLEM 5 =======================================")
    qualifyingProblem5(arrayOf())
}


/**
 * Qualifying Problem 1 - Vestigium:
 *
 * Input: an NxN matrix of natural numbers in the form of N followed by each row of the matrix as a string
 * Output: the trace (the sum of the leading diagonal - top left to bottom right),
 *         the number of rows which contain at least one duplicated number,
 *         the number of columns which contain at least one duplicated number
 * Example:
 *   Input: 1\n4\n1 2 3 4\n2 1 4 3\n3 4 1 2\n4 3 2 1
 *   Output: Case #1: 4 0 0
 */
fun qualifyingProblem1(args: Array<String>) {
    val input = Scanner(
        "3\n4\n1 2 3 4\n2 1 4 3\n3 4 1 2\n4 3 2 1\n4\n2 2 2 2\n2 3 2 3\n2 2 2 3\n2 2 2 2\n3\n2 1 3\n1 3 2\n1 2 3"
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

    var completedCases = 0
    while (input.hasNextLine()) {
        val n = input.nextLine().toInt()

        val matrixInput = mutableListOf<String>()
        for (i in 0 until n) {
            if (input.hasNextLine()) {
                matrixInput.add(input.nextLine())
            }
            else {
                // Invalid number of inputs came afterwards
                return
            }
        }

        /*
         * Split the strings (on any set of consecutive of non-numerics) into a matrix
         */
        val matrix = matrixInput.map { line ->
            val split = line.split(Regex("[^0-9]+")).map { it.toInt() }
            check(split.size == n) {
                "Matrix row size incorrect for the input '$line', expected $n, got " + split.size
            }
            split
        }
        check(matrix.size == n) {
            "Matrix size incorrect, expected $n, got " + matrix.size
        }

        /*
         * Counting
         */
        var trace = 0
        // rows/columns that contain duplicates
        var rowCount = 0
        var colCount = 0

        for (i in matrix.indices) {
            trace += matrix[i][i]
            // If distinct removed a value then it contained a duplicate
            rowCount += if (matrix[i].distinct().size != n) 1 else 0
            colCount += if (matrix.map { it[i] }.distinct().size != n) 1 else 0
        }

        /*
         * Finally
         */
        completedCases++
        println("Case #$completedCases: $trace $rowCount $colCount")
    }

    check(testCases == completedCases) {
        "Incorrect number of outputs, expected $testCases, got $completedCases"
    }
}


/**
 * Qualifying Problem 2 - Nesting Depth:
 *
 * Input: A string of numbers (0-9)
 * Output: The inputted string where
 *           - Each digit, x, in the string is nested within x sets of parentheses
 *           - Removing all parentheses from the output results in the inputted string
 *           - The length of the output is minimal
 * Example:
 *   Input: 1\n021
 *   Output: Case #1: 0((2)1)
 */
fun qualifyingProblem2(args: Array<String>) {
    val answer = "4\n0((2)1)\n(((3))1(2))\n((((4))))\n((2))((2))(1)"
    val raw = answer.replace(Regex("[()]"), "")
//    val raw = "4\n0000\n101\n111000\n1"
    val input = Scanner(raw)
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()
    var completedCases = 0

    while (input.hasNextLine()) {
        val line = input.nextLine()
        val output = StringBuilder()

        var previousN = 0
        for (char in line) {
            // For non-numerics, just maintain the string
            if (char.isDigit()) {
                // Change the nest level by the difference between this number and the previous one
                //   e.g. previous 3, current 1 = close 2 parentheses
                val n = Character.getNumericValue(char)
                val difference = abs(previousN - n)
                when {
                    n > previousN -> output.append("(".repeat(difference))
                    n < previousN -> output.append(")".repeat(difference))
                }
                previousN = n
            }

            output.append(char)
        }
        // Close all of the last number's parentheses
        output.append(")".repeat(previousN))

        completedCases++
        println("Case #$completedCases: $output")
    }
    check(testCases == completedCases) {
        "Incorrect number of outputs, expected $testCases, got $completedCases"
    }
}


/**
 * Qualifying Problem 3 - Parenting Partner Returns:
 *
 * Input: N a number of activities followed by N activities consisting of a start time and an end time
 *          Start and end times are represented as minutes since midnight
 * Output: "IMPOSSIBLE" if no scheduling is possible
 *         An N length string where
 *           - the ith character represents the ith activity
 *           - each character is either "J" or "C" for who did the activity
 *           - neither person is schedule to be doing two activities at once
 * Example:
 *   Input: 1\n3\n360 480\n420 540\n600 660
 *   Output: Case #1: JCJ
 */
fun qualifyingProblem3(args: Array<String>) {
    val input = Scanner(
        "4\n3\n360 480\n420 540\n600 660\n3\n0 1440\n1 3\n2 4\n5\n99 150\n1 100\n100 301\n2 5\n150 250\n" +
                "2\n0 720\n720 1440"
    )
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()
    var completedCases = 0

    while (input.hasNextLine()) {
        val size = input.nextLine().toInt()

        /*
         * Read in all activities
         */
        val activities = mutableListOf<Activity>()
        for (i in 0 until size) {
            if (input.hasNextLine()) {
                val line = input.nextLine().split(Regex("[^0-9]+")).map { it.toInt() }
                check(line.size == 2) { "Incorrect number of arguments on activity input line" }
                activities.add(Activity(line[0], line[1], i))
            }
            else {
                // Invalid number of inputs came afterwards
                return
            }
        }

        /*
         * Schedule calculation
         */
        activities.sortBy { it.startTime }
        activities[0].assignee = true
        // Skip the first item in the list because it's automatically assigned
        for (activity in activities.subList(1, activities.size)) {
            val clashes = activities.filter {
                it.startTime < activity.endTime && it.endTime > activity.startTime
            }
            // A list of the people doing the clash activities
            val clashesAssignees = clashes.mapNotNull { it.assignee }.distinct()

            // Both are already busy during this activity
            if (clashesAssignees.size > 1) {
                break
            }
            // One person is busy so it must be assigned to the other
            else if (clashesAssignees.size == 1) {
                activity.assignee = !clashesAssignees[0]
            }
            // Neither is busy so give it to person 1
            else {
                activity.assignee = true
            }
        }

        val output: String = when (activities.count { it.assignee == null } == 0) {
            true -> {
                activities.sortBy { it.index }
                activities.joinToString("") { if (it.assignee!!) "C" else "J" }
            }
            false -> "IMPOSSIBLE"
        }
        completedCases++
        println("Case #$completedCases: $output")
    }

    check(testCases == completedCases) {
        "Incorrect number of outputs, expected $testCases, got $completedCases"
    }
}

class Activity(
    val startTime: Int, val endTime: Int,
    val index: Int, var assignee: Boolean? = null
)


/**
 * Qualifying Problem 4 - ESAb ATAd (interactive problem):
 *
 * The server holds a database in the form of an array of bits (1s or 0s) of length B, which is "not necessarily chosen
 *   at random"
 * The program can query the server with an integer, P, between 1 and B inclusive representing an index in the database
 * A maximum of 150 queries may be made
 * Every 10 queries (i.e. 1st, 11th, 21st, etc.), one of four "quantum fluctuations" will happen to the database (each
 *   equally likely)
 *   - complement the array (every 0 becomes a 1 and vice versa)
 *   - reverse the array
 *   - complement and reverse the array
 *   - nothing happens to the array
 * These quantum fluctuations happen after the query is sent but before the response is given, i.e. when the first
 *   query is sent, the fluctuation happened, and the result from the mutated database is sent
 * The aim is to determine the *current* contents of the database
 *
 * Initial input: T, the number of test cases
 *                B, the size of the database (for the competition, this will be 10, 20, then 100)
 * Queries: send P and the judge will respond with 1 or 0 (or N if given a malformed line)
 * Test case output: the current contents of the database and the judge will respond with Y or N
 *   Sending the output does NOT count as a query. So sending the output as the 11th request would require the database
 *   state as it was after the 1st query, but sending on the 12th would be just after a fluctuation.
 * After sending the contents of the database and receiving the answer, the next test case will begin immediately
 * NOTE: after the judging system sends an N it will stop sending output. If the program doesn't recognise this, it
 *   will receive a timeout error
 *
 * ----------
 * As I did not do this round during the actual competition, I'm just going to do the input/output however I want :)
 * If I were doing this for real, I'd probably use the same structure and make my [Problem4Database] object handle all
 *   input/output beyond the initial input line
 * For a final product I'd also remove any commented out lines, but for here they're debug lines so I'm keeping them
 *
 * My solution: max size (worse case) = maxQueries - (roundUp(maxQueries / 10) - 1) * 2
 *              max size (best case) = maxQueries - (roundUp(maxQueries / 10) - 1)
 * For the 150 queries defined in the problem, a maximum database size of 122 will guarantee success
 *   For a maximum database size of 136 there is a chance of success (all mirrored pairs must be the same or different)
 * ----------
 * After wrestling with kotlin compilers I didn't manage to get it to work with the local test tool but I did manage to
 *   get it passing with the official judge so #goodEnough. Round 1A was last night and there were no interactive
 *   problems so I'm just going to roll with it for now
 */
fun qualifyingProblem4(args: Array<String>) {
    var testCaseCount = 3
    var size = 100

//    if (input.hasNextLine()) {
//        val line = input.nextLine().trim().split(" ").map { it.toInt() }
//        testCaseCount = line[0]
//        size = line[1]
//    }

    QualifyingProblem4(testCaseCount, size).run()
}
val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

/*
 * == Some musings before I start ==
 * NOTE: the 'mirror' of bit X is is bit (arrayLength - X) i.e. where this bit will end up when the array is
 *   reversed. The 'mirrored pair' is bit X and its mirror
 *
 * Size 10 is easy since you can make 10 queries and have (effectively) no quantum fluctuations
 *
 * Size 100 will be difficult since you can only make 150 queries and you must use some of them to work out what the
 * fluctuation was. You have to query every at least once bit to work out the array so that's 100 queries. This
 * leaves 50 queries to work out the fluctuation each time. Assuming it takes all 150 queries to work it out, that's
 * 14 fluctuations that must be deduced (first one is on query 11, last one on 140), so I have 50 queries to work
 * out 14 fluctuations, enough for 3 queries per round and 4 for some rounds.
 *
 *
 * How many bits do I need to query to identify each fluctuation?
 *
 * I'm pretty sure I need to query each side of the array (so the first 5 queries are the first 5 bits, the next 5
 * are the last 5 bits. This should help in identifying reversals.
 *
 * If the first and last 5 bits are all the same, only need to query 1 bit to find the fluctuation (only need to
 *   find out if a complement happened)
 *
 *
 * Testing with array size 4:
 *
 * 0111 (rev: 1110, comp: 1000, both: 0001). If I can find two bits that are the same, where their mirror two
 *   bits are different, I can make just two queries to work out the fluctuation. You can see that by querying the
 *   first two bits, there's a different answer for each fluctuation
 *
 * 0110 (rev: 0110, comp: 1001, both: 1001). Can't identify reversals
 *
 * 0101 (rev: 1010, comp: 1010, both: 0101). Can't differentiate between a reversal and a complement, and nothing
 *   and both
 *
 *
 * Conclusion: I need to be able to find a pair of a 1 and a 0 where their mirrors are both either 1 or 0. Just
 *   realised they don't have to be next to each other
 */

/**
 * I put this one in a class because it was nicer to split it out and have some global variables
 */
class QualifyingProblem4(
    private val testCaseCount: Int, private val size: Int, private var db: Problem4StdOutDatabase = Problem4Database(1)
) {
    private var currentState = arrayOfNulls<Boolean?>(size).toMutableList()

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
        for (testCaseNumber in 0 until (if (db is Problem4DebugDatabase) 1 else testCaseCount)) {
            /*
             * Reset the state
             */
            if (db !is Problem4Database) {
                db = Problem4StdOutDatabase()
            }
            if (db !is Problem4DebugDatabase) {
                db = Problem4Database(size)
            }
            currentState = arrayOfNulls<Boolean?>(size).toMutableList()

            nextNullFromStart = 0
            nextNullFromEnd = size - 1
            nextNullIsStart = true

            zeroCheckBit = -1
            oneCheckBit = -1
            isThreeZeros = true

            /*
             * Initial 10 queries are free - the first fluctuation happens before the first item is given so can be
             *   ignored
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
            if (db is Problem4Database) {
                require(result) { "Guess was incorrect" }
                println("Guess is correct! Queries used: ${db.queries}. Database size: $size")
            }
            else if (!result) {
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


    open class Problem4StdOutDatabase {
        var queries = 0
            protected set

        open fun getBit(index: Int): Boolean {
            check(index >= 0) { "Query is bad and you should feel bad" }
            check(queries <= 150) { "No more queries allowed" }

            queries++
            return transact((index + 1).toString())
        }

        open fun isDatabaseCorrect(guess: List<Boolean>): Boolean {
            return transact(databaseToString(guess))
        }

        private fun transact(output: String): Boolean {
            println(output)
            check(input.hasNextLine()) { "No reply received from server" }
            val line = input.nextLine().trim()
            return line == "Y" || line == "1"
        }

        protected fun databaseToString(list: List<Boolean?>): String {
            return list.joinToString("") {
                when (it) {
                    null -> "-"
                    true -> "1"
                    false -> "0"
                }
            }
        }

        open fun isPartialDatabaseCorrect(guess: List<Boolean?>) {
            throw NotImplementedError("Cannot do a partial check with the actual judging system")
        }
    }

    /**
     * Randomly generate a database for querying (used for testing)
     *
     * @param size the size of the database
     */
    open class Problem4Database(size: Int): Problem4StdOutDatabase() {
        protected var database: List<Boolean>

        init {
            val databaseConstruct = mutableListOf<Boolean>()
            for (i in 0 until size) {
                databaseConstruct.add(Random().nextBoolean())
            }
            database = databaseConstruct
//        println(databaseToString(database))
        }

        /**
         * @param index the index of the desired bit in the array
         */
        override fun getBit(index: Int): Boolean {
            check(index >= 0 && index < database.size) { "Query is bad and you should feel bad" }
            check(queries <= 150) { "No more queries allowed" }

            queries++
            if (queries % 10 == 1) {
//            print("Quantum fluctuation:")
                if (complementFluctuation()) {
//                print("c")
                    database = database.map { it.not() }
                }
                if (reverseFluctuation()) {
//                print("r")
                    database = database.reversed()
                }
//            println()
            }
            return database[index]
        }

        open fun reverseFluctuation(): Boolean {
            return Random().nextBoolean()
        }

        open fun complementFluctuation(): Boolean {
            return Random().nextBoolean()
        }

        override fun isDatabaseCorrect(guess: List<Boolean>): Boolean {
            return database == guess
        }

        /**
         * Function is normally unused, but I'm keeping it for the sake of debugging
         */
        override fun isPartialDatabaseCorrect(guess: List<Boolean?>) {
            for (i in database.indices) {
                if (guess[i] != null) {
                    require(guess[i] == database[i]) {
                        "Partial guess was incorrect:\n" + databaseToString(database) + "\n" + databaseToString(guess)
                    }
                }
            }
            println("Partial guess is correct so far")
        }
    }

    /**
     * Used to simulate a particular database state and set of quantum fluctuations
     *
     * @param size ensure the database is this size
     * @param databaseStr a string at least as long as [size] where each character is a 1 or 0
     * @param fluctuations item i contains an "r" if the ith fluctuation should perform a reverse ("c" for complement).
     *   After all values in the array are used, the fluctuations go back to being random
     */
    class Problem4DebugDatabase(
        size: Int, databaseStr: String, private val fluctuations: List<String>
    ) : Problem4Database(size) {
        init {
            require(databaseStr.length == size)
            val tempDatabase = (databaseStr).split("").map { it == "1" }.toMutableList()
            tempDatabase.removeAt(0)
            tempDatabase.removeAt(100)
            database = tempDatabase
            println("Actual: $databaseStr Size: " + database.size)
        }

        private var reverseFluctuationsCount = 0
        private var complementFluctuationsCount = 0

        override fun reverseFluctuation(): Boolean {
            return fluctuation("r", reverseFluctuationsCount++)
        }

        override fun complementFluctuation(): Boolean {
            return fluctuation("c", complementFluctuationsCount++)
        }

        private fun fluctuation(testLetter: String, index: Int): Boolean {
            return if (fluctuations.size > index) {
                fluctuations[index].contains(testLetter)
            }
            else {
                Random().nextBoolean()
            }
        }
    }
}


/**
 * Qualifying Problem 5 - Indicium:
 * A latin square is an NxN matrix where each row and column contains the numbers 1 to N exactly once
 *
 * Input: N, the desired size of the NxN latin square and
 *        K, the desired trace (the sum of the leading diagonal - top left to bottom right)
 * Output: Whether is is "POSSIBLE" or "IMPOSSIBLE" to create a latin square
 *           using only natural numbers with the given trace. If it is possible,
 *           this is followed by each row of the latin square as a string
 * Example:
 *   Input: 2\n3 6\n2 3
 *   Output: Case #1: POSSIBLE\n2 1 3\n3 2 1\n1 3 2\nCase #2: IMPOSSIBLE\n
 *
 * ----------
 * Note: I did not manage to pass this problem
 * Ideas for improvement (or making it far worse, who knows):
 *   - Compare all rows against all rows rather than looping through the rows comparing each to all other rows and
 *       swapping
 *   - Swap columns too
 *   - Have one or two attempts at generating a random latin square
 * ----------
 * This youtube video explains a more elegant solution to the problem
 * https://www.youtube.com/watch?v=VayKvCg4vvQ
 */
fun qualifyingProblem5(args: Array<String>) {
    val input = Scanner("2\n3 6\n2 3")
//    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))

    // No Input
    if (!input.hasNextLine()) {
        return
    }

    // nextLine rather than nextInt because I don't know if next int would
    //   mess up the next call of next in due to the \n not being consumed
    val testCases = input.nextLine().toInt()
    var completedCases = 0

    while (input.hasNextLine()) {
        val line = input.nextLine().split(Regex("[^0-9]+")).map { it.toInt() }
        val size = line[0]
        val trace = line[1]
        var possible = false

        /*
         * Generate a latin square where
         *      all rows are the numbers in descending order (wrapping around)
         *      each row is a right shift of the previous
         */
        val square = mutableListOf(IntRange(1, size).reversed().toList())
        for (i in 1 until size) {
            val newRow = square[i - 1].toMutableList()
            newRow.add(0, newRow[size - 1])
            newRow.removeAt(size)
            square.add(newRow)
        }

        /*
         * Swap rows until the trace is reached (kinda dumb, it's all I've got right now)
         */
        var currentTrace = size * size
        // Somewhat randomly chosen number of iterations
        for (i in 0..(size * 2)) {
            if (currentTrace - trace == 0) {
                possible = true
                break
            }
            // Ensure a valid row
            val row = i % (size)

            /*
             * Find the row to swap
             */
            // Duplicate the squares array pairing each row with its index, then remove the current row
            val indexed = square.mapIndexed { j, list -> list to j }.filterIndexed { index, _ -> index != row }
            // Calculate the difference to the trace should the current row and the given row be swapped
            val traceChange = indexed.map {
                (it.first[row] + square[row][it.second] - square[row][row] - it.first[it.second]) to it.second
            }
            // Find the swap where the new trace is as close to the target as possible
            val swapRowValAndIndex = traceChange.minBy { abs(currentTrace - trace - it.first) }!!

            /*
             * Swap it
             */
            val swapRow = square[swapRowValAndIndex.second]
            val originalRow = square[row]
            square.removeAt(row)
            square.add(row, swapRow)
            square.removeAt(swapRowValAndIndex.second)
            square.add(swapRowValAndIndex.second, originalRow)

            // Update current trace
            currentTrace += swapRowValAndIndex.first
        }

        completedCases++
        println("Case #$completedCases: " + if (possible) "POSSIBLE" else "IMPOSSIBLE")
        if (possible) {
            for (squareRow in square) {
                println(squareRow.joinToString(" "))
            }
        }
    }

    check(testCases == completedCases) {
        "Incorrect number of outputs, expected $testCases, got $completedCases"
    }
}