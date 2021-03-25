package googleCodeJam.mar2020

import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * Google's 2020 Code Jam - Round 1B (see QualifyingRound.kt for general competition info)
 *
 * During the competition:
 *  - I spent 2 hours of the given 2 and a half on problem 1 and managed to almost execute my idea
 *  - Went and had a look at problems 2 and 3 knowing I'd spent too long on problem 1, mused over them, and went for
 *      dinner
 */
fun main() {
    quickCalcCoordinate("NSNNWEEW")
    round1bProblem1(arrayOf())
}

/**
 * A helper function to generate arbitrary, valid coordinates for problem1
 */
fun quickCalcCoordinate(directions: String) {
    var jumps = 0
    var x = 0
    var y = 0
    for (i in directions) {
        val jump = 2.0.pow(jumps).toInt()
        jumps++
        print("$jump ")

        when (i.toUpperCase()) {
            'N' -> {
                y += jump
            }
            'S' -> {
                y -= jump
            }
            'E' -> {
                x += jump
            }
            'W' -> {
                x -= jump
            }
        }
    }
    println("\n$x $y")
}

// The maximum jump in the current set
var maxJump = -1

/**
 * Round 1B Problem 1 - Expogo:
 *
 * You stand at point (0,0) in your infinite 2D backyard
 * You are trying to reach point (x,y) with your pogo stick whose ith jump will take you 2^(i-1) in the direction of
 *   your choosing (N, S, E, or W)
 *
 * Input: Integers X and Y of the goal point on a single line
 *      Test set 1: X and Y can each be max +-4
 *      Test set 2: X and Y can each be max +-100
 *      Test set 3: X and Y can each be max +-10^9
 * Output: A string where the ith character represents the direction of the ith jump or IMPOSSIBLE if it's not possible
 *
 * ---------------------
 *
 * Musings before I start:
 *
 * If both numbers are odd it's impossible because all powers of 2 after 2^0 will be even
 * I need to break each coordinate into powers of two and see what remains
 *
 * ---------------------
 *
 * I managed to finish the idea I had after an extra hour
 * Sadly, it only passed test set 1 and gave a wrong answer on test set 2
 * Can't say I'm too surprised. I knew my solution wasn't handling the negatives jumps very well since it's searching
 *   for the minimal number of jumps to make abs(x) + abs(y) then splitting it into X and Y
 *
 * Looking at the test analysis it appears that the best solution was to do a breadth-first search, which I'd initially
 *   ruled out because I thought it would take a lot of time
 */
fun round1bProblem1(args: Array<String>) {
    val input = Scanner(
        "1\n-48 11"
//        "5\n2 3\n-2 -3\n3 0\n-1 1\n-48 11"
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
        val line = input.nextLine().split(" ").map { it.toInt() }
        val x = line[0]
        val y = line[1]
        var output = ""
        var isPossible = false

        // Should be impossible according to the start criterion but why not do it anyway
        if (x == 0 && y == 0) {
            isPossible = true
        }
        // One must be even, the other must be odd
        else if (abs(x + y) % 2 == 1) {
            isPossible = true

            val positiveJumps = mutableSetOf<Int>()
            val negativeJumps = mutableSetOf<Int>()
            var goal = abs(x) + abs(y)
            while (positiveJumps.sum() < goal) {
                positiveJumps.add(2.0.pow(positiveJumps.size).toInt())
            }
            maxJump = positiveJumps.max()!!
            goal -= positiveJumps.sum()

            val lastChanged = mutableListOf<Int>()
            while (goal != 0 && isPossible) {
                val swap: Int
                // If the goal is < 0 we've overshot and need to change the positive to a negative jump
                if (goal < 0 && positiveJumps.isNotEmpty()) {
                    // - it - it because we're removing the original jump then doing that jump as negative
                    swap = positiveJumps.map { abs(goal + it + it) to it }.minBy { it.first }!!.second
                    goal += swap * 2
                    positiveJumps.remove(swap)
                    negativeJumps.add(swap)
                }
                else if (goal > 0 && negativeJumps.isNotEmpty()) {
                    swap = negativeJumps.map { abs(goal - it - it) to it }.minBy { it.first }!!.second
                    goal -= swap * 2
                    negativeJumps.remove(swap)
                    positiveJumps.add(swap)
                }
                else {
                    println("impossible1")
                    isPossible = false
                    break
                }
                lastChanged.add(swap)
                if (lastChanged.size > 4) {
                    lastChanged.removeAt(0)
                    if (lastChanged.distinct().size <= 2) {
                        println("lastChangedExceed1")
                        isPossible = false
                        break
                    }
                }
            }

            // Trying to get them to 0 (the direction can be flipped later)
            val yPositive = mutableSetOf<Int>()
            val yNegative = mutableSetOf<Int>()

            // Move the 1 to the correct side
            if (abs(y) % 2 == 1) {
                if (positiveJumps.contains(1)) {
                    yPositive.add(1)
                    positiveJumps.remove(1)
                }
                else {
                    yNegative.add(1)
                    negativeJumps.remove(1)
                }
            }
            var xGoal = abs(x) - positiveJumps.sum() + negativeJumps.sum()
            var yGoal = abs(y) - yPositive.sum() + yNegative.sum()

            while (xGoal != 0 && yGoal != 0 && isPossible) {
                var posSwap: Pair<Int, Int>? = null
                var negSwap: Pair<Int, Int>? = null
                var swap: Int? = null
                // If the goal is < 0 we've overshot and need to either steal one of y's negatives or give y one of x's positives
                if ((xGoal < 0 || xGoal == 0 && yGoal > 0) && (positiveJumps.size + yNegative.size) > 0) {
                    if (positiveJumps.isNotEmpty()) {
                        val test = positiveJumps.map { findTotalDifference(it, xGoal, yGoal) to it }
                        posSwap = positiveJumps.map { findTotalDifference(it, xGoal, yGoal) to it }.minBy { it.first }!!
                    }
                    if (yNegative.isNotEmpty()) {
                        val test = yNegative.map { findTotalDifference(it, yGoal, xGoal) to it }
                        negSwap = yNegative.map { findTotalDifference(it, yGoal, xGoal) to it }.minBy { it.first }!!
                    }
                    swap = swap(posSwap, negSwap, positiveJumps, yNegative, yPositive, negativeJumps)
                }
                else if ((xGoal > 0 || xGoal == 0 && yGoal < 0) && (negativeJumps.size + yPositive.size) > 0) {
                    if (yPositive.isNotEmpty()) {
                        val test = yPositive.map { findTotalDifference(it, yGoal, xGoal) to it }
                        posSwap = yPositive.map { findTotalDifference(it, yGoal, xGoal) to it }.minBy { it.first }!!
                    }
                    if (negativeJumps.isNotEmpty()) {
                        val test = negativeJumps.map { findTotalDifference(it, xGoal, yGoal) to it }
                        negSwap = negativeJumps.map { findTotalDifference(it, xGoal, yGoal) to it }.minBy { it.first }!!
                    }
                    swap = swap(posSwap, negSwap, yPositive, negativeJumps, positiveJumps, yNegative)
                }
                else if (xGoal != 0) {
                    println("impossible2")
                    isPossible = false
                    break
                }

                if (swap != null) {
                    lastChanged.add(swap)
                    if (lastChanged.size > 4) {
                        lastChanged.removeAt(0)
                        if (lastChanged.distinct().size <= 2) {
                            println("lastChangedExceed2")
                            isPossible = false
                            break
                        }
                    }
                }
                xGoal = abs(x) - positiveJumps.sum() + negativeJumps.sum()
                yGoal = abs(y) - yPositive.sum() + yNegative.sum()
            }

            if (isPossible) {
                val amalgamate = mutableListOf<Pair<Int, String>>()
                amalgamate.addAll((if (x > 0) positiveJumps else negativeJumps).map { it to "E" })
                amalgamate.addAll((if (x > 0) negativeJumps else positiveJumps).map { it to "W" })
                amalgamate.addAll((if (y > 0) yPositive else yNegative).map { it to "N" })
                amalgamate.addAll((if (y > 0) yNegative else yPositive).map { it to "S" })
                amalgamate.sortBy { it.first }
                output = amalgamate.joinToString("") { it.second }
            }
        }

        println("Case #$completedCases: " + if (isPossible) output else "IMPOSSIBLE")
    }
}

fun findTotalDifference(value: Int, addToGoal: Int, subFromGoal: Int): Int {
    require(maxJump != -1)
    // Given 1 has been placed in the correct list, it should never move, therefore make it stupid high
    if (value == 1) {
        return abs(addToGoal) + abs(subFromGoal) + maxJump * 2
    }
    return abs(addToGoal + value) + abs(subFromGoal - value)
}

fun swap(
    posSwap: Pair<Int, Int>?, negSwap: Pair<Int, Int>?, positiveFrom: MutableSet<Int>, negativeFrom: MutableSet<Int>,
    positiveTo: MutableSet<Int>, negativeTo: MutableSet<Int>
): Int {
    require(posSwap != null || negSwap != null)
    if (negSwap == null || posSwap!!.first <= negSwap.first) {
        positiveFrom.remove(posSwap!!.second)
        positiveTo.add(posSwap.second)
        return posSwap.second
    }
    else {
        negativeFrom.remove(negSwap.second)
        negativeTo.add(negSwap.second)
        return negSwap.second
    }
}

/**
 * Round 1B Problem 3 - Blindfolded Bullseye (interactive problem):
 *
 * There is a square wall that is 2x10^9 high with a dartboard on it. The dartboard has a radius of somewhere between A
 *   and B and no part of dartboard exceeds the perimeter of the wall. You are blindfolded and have 300 darts to throw,
 *   with the aim of hitting the centre. You will be told whether you hit the centre, hit the dartboard, or missed. You
 *   can throw with extreme accuracy despite being blindfolded and they will go where you throw them.
 *
 * Test set 1: A = B = 10^9 - 5.
 * Test set 2: A = B = 10^9 - 50.
 * Test set 3: A = 10^9 / 2, B = 10^9
 *
 * Initial input: T, the number of test cases
 *                A, the minimum radius the dartboard could be
 *                B, the maximum radius the dartboard could be
 * Queries: Send X Y, the coordinates for the dart to hit where 0 0 is the centre of the wall
 *          The judge will respond with
 *            CENTER for the centre of the dartboard
 *            HIT if 0 < (X - Xi)^2 + (Y - Yi)^2 ≤ radius^2
 *            WRONG if the request is wrong
 *            MISS in all other cases
 *
 * ---------------------
 *
 * Musings:
 * I think I'd start by throwing up to 9 darts to find the board. Split the wall into 16 squares and throw a dart at the
 *   points where 4 squares meet
 * From there I'd try to find three edges of the board given a circle can be defined using 3 points.
 * To find each edge, I'd go out from one of the points that I've hit to an edge (or a point I missed) in a binary
 *   search (send a dart halfway between the points each time) until I find the edge
 * From there the centre of the circle should be easy to calculate with some maths stolen from Google
 *
 * ---------------------
 *
 * Looking at the analysis of the problem, the error in calculating the centre of the circle could be an issue given the
 *   precision isn't high. I had wondered about precision but I thought I'd cross that bridge if/when I got to it. The
 *   simple solution given is to just calculate error along with the circle's centre and check other points close to the
 *   calculated centre.
 * Ooh reading more, there's an easier way of doing it using the binary search. Take a given Y and use a binary search
 *   to find the two bounds of the circle on that line. Then do the same for an X. The centre is now the mid point of
 *   the two points found from the Y and the mid point of the two points found from the X. No maths to google, noice.
 *   This is also guaranteed to be within the 300 dart limit (31 for each point, plus as many as you need to hit the
 *   dartboard for the first time)
 */
fun round1bProblem2(args: Array<String>) {

}

/**
 * Round 1B Problem 3 - Join the Ranks:
 *
 * You are given a brand new pack of cards where the card values are 1 to R and the suits are 1 to S
 * They are sorted as 1-R for suit 1 first, then 1-R for suit 2 etc.
 * The goal is to sort the cards so that all cards with value 1 are first, then all with value 2, and so on (suits
 *   don't matter)
 * The only way to rearrange the deck is to swap the first X cards with the next Y cards (i.e. draw X cards from the top
 *   of the deck and place them in pile A, draw Y cards from the top of the deck and place them on pile B, place pile A
 *   back on the deck, place pile B back on the deck)
 *
 * Input: R, the number of values/ranks, and S, the number of suits
 * Output: The number of shuffle motions required to sort the deck followed by each shuffle as a new line stating X Y
 * Example:
 *   Input: 1\n2 3
 *   Output: Case #1: 2\n2 3\n2 2
 *
 *  ---------------------
 *
 * Musings:
 * I guess I'd start by trying to find the first instance of the card ranked R and move it to the beginning of the cards
 *   ranked R. So the first X would be R (entire first suit, which ends in the first card ranked R) and the first Y
 *   would be (R * S) - R - 1 (total cards - A pile size - 1 to remove the last card which is also ranked R). I'd then
 *   try to iterate on this, getting all the R ranked cards to the end, then the R-1 ranked cards, and so on.
 * I think I'd keep track of where cards are by just having an array which is the current deck which would make it
 *   easier to calculate the size of A and B
 *
 * ---------------------
 *
 * Oof, looking at the analysis they're doing another breadth-first search. Didn't read that much of it but I'd guess
 *   the heuristic is how sorted the deck is
 */
fun round1bProblem3(args: Array<String>) {

}