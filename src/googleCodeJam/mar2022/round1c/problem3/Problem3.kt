package googleCodeJam.mar2022.round1c.problem3

import googleCodeJam.mar2022.IoHandler
import googleCodeJam.mar2022.IoHandler.Companion.checkExpectedOutput
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.properties.Delegates


fun main() {
    val ioHandler = IoHandler(
            "src/googleCodeJam/mar2022/round1c/problem3/test_data",
            1,
            true
    )
    Problem3.execute(ioHandler)
}

object Problem3 {
    private var totalModules by Delegates.notNull<Int>()
    private var totalSubnets by Delegates.notNull<Int>()
    private val checkedSubnetLists = mutableSetOf<Set<Int>>()

    fun execute(ioHandler: IoHandler? = null) {
        val input = ioHandler?.input ?: Scanner(System.`in`)
        val output = ioHandler?.expectedOutput

        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedOutput = output?.nextLine()
            val inputs = input.nextLine().split(" ").map { it.toInt() }
            require(inputs.size == 2) { "Invalid inputs" }
            totalModules = inputs[0]
            totalSubnets = inputs[1]

            val totalValid = (ceil(totalModules / 2.0).toInt()..totalModules).fold(0) { acc, i ->
                acc + searchSubnets(setOf(i), 0)
            }
            val totalPossible = (totalModules * (totalModules - 1) / 2).factorial()

            val outputString = "Case #$testCaseIndex: "
            expectedOutput?.checkExpectedOutput(outputString, true)
            println(outputString)
        }
    }

    private fun Int.factorial() = (1..this).fold(1) { acc, i -> acc * i }

    /**
     * @param combinations total combinations to create the current [subnetList]
     */
    private fun searchSubnets(
        subnetList: Set<Int>,
        combinations: Int
    ): Int {
        val remainingModules = totalModules - subnetList.sum()
        val remainingSubnets = totalSubnets - subnetList.size

        // Not enough modules remaining to create the required number of subnets
        if (remainingModules < remainingSubnets) {
            return 0
        }

        var total = 0
        val maximumSubnetSize = remainingModules - remainingSubnets + 1
        val minimumSubnetSize = ceil(remainingModules / remainingSubnets.toDouble()).toInt()
        for (i in maximumSubnetSize.rangeTo(minimumSubnetSize)) {
            if (remainingModules < i
                    || (remainingModules == i && remainingSubnets != 1)
            ) {
                continue
            }
            val newSubnetList = subnetList.plus(i)
            val newCombinations = combinations + remainingModules.choose(i)
            if (remainingModules != i) {
                searchSubnets(newSubnetList, newCombinations)
            }
            if (!checkedSubnetLists.add(newSubnetList)) {
                continue
            }

            // Probability all subnets are distinct
            val probability = newSubnetList.fold(1f) { acc, item ->
                acc * (item - 1f) / (totalModules - 1f)
            }
            val actualMatches = probability * newCombinations
            val roundedMatches = actualMatches.roundToInt()
            check(actualMatches == roundedMatches.toFloat()) { "Actual is not a whole number" }
            total += roundedMatches
        }
        return total
    }

    private fun Int.choose(choose: Int): Int {
        return this.factorial() / (choose.factorial() * (this - choose).factorial())
    }
}