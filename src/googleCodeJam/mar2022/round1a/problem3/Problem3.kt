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
                Weights(input.nextLine().split(" ").map { it.toInt() })
            }
            check(exercises.all { it.size == totalTypesOfWeights }) { "Invalid weight detected" }

            var totalOperations = 0
            var currentWeightStack = mutableListOf<Weights>()
            exercises.forEach { exercise ->
                /*
                 * If there are no weights on the bar, add all exercise weights
                 */
                if (currentWeightStack.isEmpty()) {
                    currentWeightStack.add(exercise)
                    totalOperations += exercise.sum
                    return@forEach
                }

                /*
                 * Work out what weights can be kept on the bar
                 */
                var remainingWeightsToBeAdded = exercise
                for (weightsSection in currentWeightStack.withIndex()) {
                    val compareWeightsReturn = weightsSection.value.compareWeights(remainingWeightsToBeAdded)

                    /*
                     * If weights from this section need to be removed, all later sections must be removed in their entirety
                     * For the moment we are also assuming the current section is completely removed too
                     */
                    if (compareWeightsReturn.weightsToRemove.sum > 0) {
                        totalOperations += currentWeightStack
                                .subList(weightsSection.index, currentWeightStack.lastIndex)
                                .sumBy { it.sum }
                        currentWeightStack = currentWeightStack.take(weightsSection.index + 1).toMutableList()
                        break
                    }
                    remainingWeightsToBeAdded = compareWeightsReturn.weightsToAdd
                }

                /*
                 * Add the outstanding weights to the bar
                 */
                val weightsToAdd = remainingWeightsToBeAdded.sum
                if (weightsToAdd > 0) {
                    currentWeightStack.add(remainingWeightsToBeAdded)
                    totalOperations += weightsToAdd
                }
            }

            // Remove all weights from the bar to end
            totalOperations += currentWeightStack.sumBy { it.sum }

            val outputString = "Case #$testCaseIndex: $totalOperations"
            expectedOutput?.checkExpectedOutput(outputString, true)
            println(outputString)
        }
    }

    data class CompareWeightsReturn(
        val weightsToAdd: Weights,
        val weightsToRemove: Weights
    )

    class Weights(
        weights: List<Int>
    ) {
        val size = weights.size
        private val weights = weights.toMutableList()
        val sum = weights.sum()

        /**
         * @return what weights need to be added/removed from this set to create [desiredWeights]
         */
        fun compareWeights(desiredWeights: Weights): CompareWeightsReturn {
            val weightsToAdd = mutableListOf<Int>()
            val weightsToRemove = mutableListOf<Int>()
            for (weightType in desiredWeights.weights.indices) {
                val onBar = weights[weightType]
                val forExercise = desiredWeights.weights[weightType]
                weightsToRemove.add(if (onBar < forExercise) 0 else onBar - forExercise)
                weightsToAdd.add(if (onBar > forExercise) 0 else forExercise - onBar)
            }
            return CompareWeightsReturn(Weights(weightsToAdd), Weights(weightsToRemove))
        }
    }
}