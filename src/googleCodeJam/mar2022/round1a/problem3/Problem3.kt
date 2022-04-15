package googleCodeJam.mar2022.round1a.problem3

import googleCodeJam.mar2022.IoHandler
import googleCodeJam.mar2022.IoHandler.Companion.checkExpectedOutput
import java.util.*

fun main() {
    val ioHandler = IoHandler(
            "src/googleCodeJam/mar2022/round1a/problem3/test_data",
            0,
            true
    )
    Problem3.execute(ioHandler)
}

object Problem3 {
    fun execute(ioHandler: IoHandler? = null) {
        val input = ioHandler?.input ?: Scanner(System.`in`)
        val output = ioHandler?.expectedOutput

        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedOutput = output?.nextLine()

            val (totalExercises, totalTypesOfWeights) = input.nextLine()
                    .split(" ")
                    .let { it[0].toInt() to it[1].toInt() }
            val exercises = List(totalExercises) {
                input.nextLine().split(" ").map { it.toInt() }
            }
            check(exercises.all { it.size == totalTypesOfWeights }) { "Invalid weight detected" }

            var totalOperations = 0
            val currentWeightStack = mutableListOf<MutableList<Int>>()
            exercises.forEach { exercise ->
                if (currentWeightStack.isEmpty()) {
                    currentWeightStack.add(exercise.toMutableList())
                    totalOperations += exercise.sum()
                    return@forEach
                }
                val compareWeightsReturn = compareWeights(currentWeightStack.flattenWeights(), exercise)
                currentWeightStack.add(compareWeightsReturn.weightsToAdd.toMutableList())
                totalOperations += compareWeightsReturn.weightsToAdd.sum()
            }

            // Remove all weights from the bar to end
            totalOperations += currentWeightStack.flatten().sum()

            val outputString = "Case #$testCaseIndex: $totalOperations"
            expectedOutput?.checkExpectedOutput(outputString, true)
            println(outputString)
        }
    }

    fun compareWeights(weightsOnBar: List<Int>, weightsForExercise: List<Int>): CompareWeightsReturn {
        val weightsToAdd = mutableListOf<Int>()
        val weightsToRemove = mutableListOf<Int>()
        for (weightType in weightsForExercise.indices) {
            val onBar = weightsOnBar[weightType]
            val forExercise = weightsForExercise[weightType]
            weightsToRemove.add(if (onBar < forExercise) 0 else onBar - forExercise)
            weightsToAdd.add(if (onBar > forExercise) 0 else forExercise - onBar)
        }
        return CompareWeightsReturn(weightsToAdd, weightsToRemove)
    }

    data class CompareWeightsReturn(
        val weightsToAdd: List<Int>,
        val weightsToRemove: List<Int>
    )

    fun List<List<Int>>.flattenWeights() = List(first().size) { index -> sumBy { it[index] } }
}