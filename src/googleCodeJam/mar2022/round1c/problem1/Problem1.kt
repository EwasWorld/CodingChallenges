package googleCodeJam.mar2022.round1c.problem1

import googleCodeJam.mar2022.IoHandler
import googleCodeJam.mar2022.IoHandler.Companion.checkExpectedOutput
import java.util.*


fun main() {
    val ioHandler = IoHandler(
            "src/googleCodeJam/mar2022/round1c/problem1/test_data",
            2,
            false
    )
    Problem1.execute(ioHandler)
}

object Problem1 {
    private lateinit var towersInput: List<String>

    fun execute(ioHandler: IoHandler? = null) {
        val input = ioHandler?.input ?: Scanner(System.`in`)
        val output = ioHandler?.expectedOutput

        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedOutput = output?.nextLine()
            val numberOfTowers = input.nextLine().toInt()
            towersInput = input.nextLine().split(" ")
            check(towersInput.size == numberOfTowers) { "Invalid input" }

            // Check whether this is a faster way to group the towers?
//            val towersGrouped = mutableMapOf<Char, MutableList<Int>>()
//            for ((index, tower) in towersInput.withIndex()) {
//                for (letter in tower.toCharArray().distinct()) {
//                    towersGrouped.getOrPut(letter, { mutableListOf() }).add(index)
//                }
//            }

            val towersGrouped = towersInput
                    .mapIndexed { index, s -> s.toCharArray().distinct().map { it to index } }
                    .flatten()
                    .groupBy { it.first }
                    .mapValues { it.value.map { pair -> pair.second } }

            var isPossible = true
            val finalTower = FinalTower()
            for ((letter, towerIndexes) in towersGrouped) {
                if (towerIndexes.isEmpty()) {
                    continue
                }
                if (towerIndexes.size == 1) {
                    val towerValidity = towersInput[towerIndexes.first()].validateTower(letter)
                    isPossible = towerValidity.isValid
                    if (isPossible) {
                        isPossible = finalTower.addTower(towerLetter = letter, repeatedLetter = towerIndexes)
                    }
                }
                else {
                    // repeatedLetterOnly: tower is just the current letter repeated
                    // otherLettersToo: tower contains other letters besides the current letter
                    val (repeatedLetterOnly, otherLettersToo) = towerIndexes.partition {
                        towersInput[it].toCharArray().distinct().size == 1
                    }
                    when (otherLettersToo.size) {
                        0 -> {
                            isPossible = finalTower.addTower(towerLetter = letter, repeatedLetter = repeatedLetterOnly)
                        }
                        1 -> {
                            val towerValidity = towersInput[otherLettersToo[0]].validateTower(letter)
                            isPossible = towerValidity.isValid
                            if (isPossible) {
                                isPossible = finalTower.addTower(
                                        towerLetter = letter,
                                        validFromEnd = if (towerValidity.validFromFront) null else otherLettersToo[0],
                                        repeatedLetter = repeatedLetterOnly,
                                        validFromFront = if (towerValidity.validFromFront) otherLettersToo[0] else null
                                )
                            }
                        }
                        2 -> {
                            val firstValidity = towersInput[otherLettersToo[0]].validateTower(letter)
                            val secondValidity = towersInput[otherLettersToo[1]].validateTower(letter)
                            if (!firstValidity.isValid || !secondValidity.isValid
                                    || setOf(firstValidity.validType, secondValidity.validType)
                                    != setOf(ValidType.FROM_FRONT, ValidType.FROM_END)
                            ) {
                                isPossible = false
                            }
                            else {
                                isPossible = finalTower.addTower(
                                        towerLetter = letter,
                                        validFromEnd = if (firstValidity.validFromFront) otherLettersToo[1] else otherLettersToo[0],
                                        repeatedLetter = repeatedLetterOnly,
                                        validFromFront = if (firstValidity.validFromFront) otherLettersToo[0] else otherLettersToo[1]
                                )
                            }
                        }
                        else -> isPossible = false
                    }
                }
                if (!isPossible) {
                    break
                }
            }

            val outputString = "Case #$testCaseIndex: " + if (isPossible) finalTower.toString() else "IMPOSSIBLE"
            outputString.checkOutput(isPossible, expectedOutput, ioHandler?.isSampleTestSet == true)
            println(outputString)
        }
    }

    private fun String.checkOutput(
        isPossible: Boolean,
        expectedOutput: String?,
        @Suppress("SameParameterValue") displayError: Boolean = false
    ) {
        try {
            val outputSplit = split(":")
            val outputTower = outputSplit[1].trim()
            if (!isPossible) {
                try {
                    expectedOutput?.checkExpectedOutput(this, true)
                    return
                }
                catch (e: Exception) {
                    throw IllegalStateException("You thought this was not possible...")
                }
            }
            check(expectedOutput != "${outputSplit[0]}: IMPOSSIBLE") { "You thought this was possible..." }

            var seenLetters = ""
            var currentLetter = '1'
            outputTower.forEach {
                if (currentLetter != it) {
                    check(!seenLetters.contains(it)) { "$it seen in multiple places" }
                    currentLetter = it
                    seenLetters += it
                }
            }

            var outputRemaining = outputTower
            towersInput.forEach {
                val index = outputRemaining.indexOf(it)
                check(index != -1) { "$it is missing" }
                outputRemaining = outputRemaining.removeRange(index, index + it.length)
            }
        }
        catch (e: Exception) {
            if (displayError) {
                throw e
            }
            throw IllegalStateException("Wrong answer")
        }
    }

    private fun String.validateTower(desiredLetter: Char): ValidateResult {
        val desiredLetterCount = filter { it == desiredLetter }.length
        if (desiredLetterCount == length) {
            return ValidateResult(true, ValidType.BOTH_ENDS)
        }

        val validFromFront: ValidType
        val testValue = when (desiredLetter) {
            first() -> {
                validFromFront = ValidType.FROM_FRONT
                this
            }
            last() -> {
                validFromFront = ValidType.FROM_END
                reversed()
            }
            else -> {
                validFromFront = ValidType.MIDDLE_ONLY
                dropWhile { it != desiredLetter }
            }
        }
        val consecutiveDesiredLetters = testValue.takeWhile { it == desiredLetter }.length
        return ValidateResult(consecutiveDesiredLetters == desiredLetterCount, validFromFront)
    }

    data class ValidateResult(val isValid: Boolean, val validType: ValidType) {
        val validFromFront = validType == ValidType.FROM_FRONT
    }

    enum class ValidType { FROM_FRONT, MIDDLE_ONLY, FROM_END, BOTH_ENDS }

    class FinalTower {
        private val partialTowers = mutableListOf<PartialTower>()

        /**
         * @return true if the tower is valid
         */
        fun addTower(
            @Suppress("UNUSED_PARAMETER") towerLetter: Char, // Used for setting conditional breakpoints :P
            validFromEnd: Int? = null,
            repeatedLetter: List<Int> = listOf(),
            validFromFront: Int? = null
        ): Boolean {
            val towerSequence = listOf(validFromEnd).plus(repeatedLetter).plus(validFromFront).filterNotNull()
            if (towerSequence.isEmpty()) {
                return true
            }

            val matchingTowers = partialTowers.filter {
                // Only checking the first and last because this partialTower's internal letters are unique
                val containsFirst = it.contains(towerSequence.first())
                val containsLast = it.contains(towerSequence.last())
                if (containsFirst && containsLast && towerSequence.first() != towerSequence.last()) {
                    // Can only 'attach' at one end so if both ends are distinct and contained in the tower,
                    //      it's impossible
                    return false
                }
                containsFirst || containsLast
            }.distinct()

            // Join this and matching towers into one
            var partialTower = PartialTower(towerSequence)
            for (matchingTower in matchingTowers) {
                partialTowers.remove(matchingTower)
                try {
                    partialTower = partialTower.combineTowers(matchingTower)
                }
                catch (e: Exception) {
                    return false
                }
            }
            partialTowers.add(partialTower)
            return true
        }

        override fun toString(): String {
            return partialTowers.joinToString("")
        }

        data class PartialTower(private val towerIndexes: List<Int>) {
            fun combineTowers(other: PartialTower): PartialTower {
                if (towerIndexes.first() == other.towerIndexes.last()) {
                    return PartialTower(other.towerIndexes.dropLast(1).plus(towerIndexes))
                }
                if (towerIndexes.last() == other.towerIndexes.first()) {
                    return PartialTower(towerIndexes.dropLast(1).plus(other.towerIndexes))
                }
                if (towerIndexes.isEmpty()
                        || towerIndexes.size == 1 && other.towerIndexes.contains(towerIndexes.first())
                ) {
                    return other
                }
                if (other.towerIndexes.isEmpty()
                        || other.towerIndexes.size == 1 && towerIndexes.contains(other.towerIndexes.first())
                ) {
                    return this
                }
                throw IllegalStateException("Cannot combine towers")
            }

            fun contains(index: Int) = towerIndexes.contains(index)

            override fun toString(): String {
                return towerIndexes.joinToString("") { towersInput[it] }
            }
        }
    }
}