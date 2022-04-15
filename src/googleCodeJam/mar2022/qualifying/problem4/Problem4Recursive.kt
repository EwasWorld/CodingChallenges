package googleCodeJam.mar2022.qualifying.problem4

import googleCodeJam.mar2022.qualifying.TimeChecker
import googleCodeJam.mar2022.qualifying.problem4.Problem4Recursive.problem4RecursiveExecute
import java.io.File
import java.util.*


fun main() {
//    val input = Scanner(System.`in`)
//    val input = Scanner(File("src\\googleCodeJam\\mar2022\\qualifying\\problem4\\testInput.txt"))
    val folder: String
    val fileNamePrefix: String
    if (true) {
        val testSet = "3"
        folder = "src\\googleCodeJam\\mar2022\\qualifying\\problem4\\test_set_$testSet\\"
        fileNamePrefix = "ts$testSet"
    }
    else {
        folder = "src\\googleCodeJam\\mar2022\\qualifying\\problem4\\sample_test_set_1\\"
        fileNamePrefix = "sample_ts1"
    }
    val input = Scanner(File("$folder${fileNamePrefix}_input.txt"))
    val expectedOutput = Scanner(File("$folder${fileNamePrefix}_output.txt"))

    val timeChecker = TimeChecker()
    problem4RecursiveExecute(input, expectedOutput)
    timeChecker.logTime(printNow = true)

    println("All passed")
}

object Problem4Recursive {
    private lateinit var funFactors: List<Long>
    private lateinit var groupedNextNodes: Map<Int?, List<Int>>

    fun problem4RecursiveExecute(input: Scanner, expectedOutput: Scanner?) {
        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedLine = expectedOutput?.nextLine()
            val totalModules = input.nextLine().toInt()
            // `fun_factors[i] = x` - node i has fun factor x
            funFactors = input.nextLine().split(" ")
                    .takeIf { it.size == totalModules }!!
                    .map { it.toLong() }
            // `next_nodes_grouped[i] = [x, y, z]` - node i is pointed to by nodes x, y, and z
            groupedNextNodes = input.nextLine().split(" ")
                    .takeIf { it.size == totalModules }!!
                    // `currentData[i] = "j"` node i points to node j - 1
                    // If j is 0, node i points to nothing
                    .map { connectionStr -> connectionStr.toInt().takeIf { it > 0 }?.minus(1) }
                    // `currentData[i] = j` - node i points to node j (j can be null)
                    .withIndex()
                    // Flip so that currentData[i] = [j] means that node i is pointed to by node j
                    .groupBy { it.value }
                    // Drop the index which is no longer needed
                    .mapValues { indexedNextNodesGroup -> indexedNextNodesGroup.value.map { it.index } }
            val finalFunSum: Long = groupedNextNodes.getValue(null)
                    .map {
                        processSubtreeRecursive(it).let { data ->
                            data.offshootsSum + data.currentNodeEffectiveFun
                        }
                    }
                    .sum()
            val output = "Case #$testCaseIndex: $finalFunSum"
            check(expectedLine == null || output == expectedLine) { "Failed $testCaseIndex: Wrong answer" }
            println(output)
        }
    }

    /**
     * Process the subtree that has [nodeIndex] at its root (i.e. [nodeIndex] and all nodes that point to it.
     * This function will be used when the node at [nodeIndex] has only one other node pointing to it.
     * If there is more than one node pointing to the node at [nodeIndex], this will call [processSubtreeRecursive].
     *
     * While [processSubtreeRecursive] could process any subtrees, it will cause a stack overflow error when processing
     * deep subtrees. This function will allow processing of deeper subtrees if they have nodes that only have one node
     * pointing to them
     */
    private fun processSubtreeIterative(nodeIndex: Int): ProcessSubtreeReturnData {
        // Given this function is only processing nodes where there's only one node pointing to them,
        // they are all part of a single chain. Therefore, only the max fun seen is needed
        var maxFun = funFactors[nodeIndex]

        var current = nodeIndex
        while (true) {
            val nodeFun = funFactors[current]

            /*
             * Check whether we have just one previous node
             */
            val allPrevious = groupedNextNodes[current] ?: return ProcessSubtreeReturnData(0, nodeFun)
            if (allPrevious.size > 1) {
                val previousData = processSubtreeRecursive(current)
                if (previousData.currentNodeEffectiveFun > maxFun) {
                    return previousData
                }
                else {
                    return ProcessSubtreeReturnData(previousData.offshootsSum, maxFun)
                }
            }

            /*
             * Update max fun and move back down the chain
             */
            val previous = allPrevious.first()
            val previousFun = funFactors[previous]
            if (previousFun > maxFun) {
                maxFun = previousFun
            }
            if (groupedNextNodes[previous] == null) {
                break
            }
            current = previous
        }
        return ProcessSubtreeReturnData(0, maxFun)
    }

    /**
     * Process the subtree that has [nodeIndex] at its root (i.e. [nodeIndex] and all nodes that point to it.
     * This function will be used when the node at [nodeIndex] has more than one other node pointing to it.
     * If there is only one node pointing to the node at [nodeIndex], this will call [processSubtreeIterative]
     */
    private fun processSubtreeRecursive(nodeIndex: Int): ProcessSubtreeReturnData {
        val nodeFun = funFactors[nodeIndex]
        val allPreviousNodeIndexes = groupedNextNodes[nodeIndex] ?: return ProcessSubtreeReturnData(0, nodeFun)
        if (allPreviousNodeIndexes.size == 1) {
            // Reduce recursion depth by only recusing when necessary
            return processSubtreeIterative(nodeIndex)
        }
        return allPreviousNodeIndexes.map { processSubtreeRecursive(it) }
                .let { previousNodeTotals ->
                    var offshootsSum = previousNodeTotals.map { it.offshootsSum }.sum()
                    val allEffectiveFunFactors =
                            previousNodeTotals.map { it.currentNodeEffectiveFun }.sorted().toMutableList()

                    // Remove the lowest value as this will be overridden by whatever the current node becomes
                    //   or will be used as the current node fun therefore shouldn't be added to the total
                    var effectiveFun = allEffectiveFunFactors.removeAt(0)
                    if (effectiveFun <= nodeFun) {
                        effectiveFun = nodeFun
                    }

                    // All remaining previous nodes' branches are effectively terminated,
                    //   either succeeded by the lowest or by current_node_fun
                    offshootsSum += allEffectiveFunFactors.sum()
                    ProcessSubtreeReturnData(offshootsSum, effectiveFun)
                }
    }

    data class ProcessSubtreeReturnData(
        /**
         * The sum of any offshoot branches that have terminated
         */
        val offshootsSum: Long,
        /**
         * The single fun factor that should be used in place of this subtree when processing nodes closer to the root
         */
        val currentNodeEffectiveFun: Long
    )
}