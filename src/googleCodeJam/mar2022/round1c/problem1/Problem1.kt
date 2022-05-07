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
//            for ((index, tower) in towers.withIndex()) {
//                for (letter in tower) {
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
                if (towerIndexes.size <= 1) {
                    val firstTower = validateTower(towersInput[towerIndexes.first()], letter)
                    isPossible = firstTower.isValid
                    if (isPossible) {
                        isPossible = finalTower.addTowers(towerLetter = letter, repeatedLetter = towerIndexes)
                    }
                }
                else {
                    val (repeated, other) = towerIndexes.partition {
                        towersInput[it].toCharArray().distinct().size == 1
                    }
                    when (other.size) {
                        0 -> {
                            isPossible = finalTower.addTowers(towerLetter = letter, repeatedLetter = repeated)
                        }
                        1 -> {
                            val firstTower = validateTower(towersInput[other[0]], letter)
                            isPossible = firstTower.isValid
                            if (isPossible) {
                                isPossible = finalTower.addTowers(
                                        towerLetter = letter,
                                        validFromEnd = if (firstTower.validFromFront) null else other[0],
                                        repeatedLetter = repeated,
                                        validFromFront = if (firstTower.validFromFront) other[0] else null
                                )
                            }
                        }
                        2 -> {
                            val firstTower = validateTower(towersInput[other[0]], letter)
                            val secondTower = validateTower(towersInput[other[1]], letter)
                            if (!firstTower.isValid || !secondTower.isValid) {
                                isPossible = false
                            }
                            else {
                                val types = listOf(firstTower.validType, secondTower.validType)
                                if (!types.contains(ValidType.FROM_FRONT) || !types.contains(ValidType.FROM_END)) {
                                    isPossible = false
                                }
                                else {
                                    isPossible = finalTower.addTowers(
                                            towerLetter = letter,
                                            validFromEnd = if (firstTower.validFromFront) other[1] else other[0],
                                            repeatedLetter = repeated,
                                            validFromFront = if (firstTower.validFromFront) other[0] else other[1]
                                    )
                                }
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
            checkOutput(outputString, isPossible, expectedOutput, ioHandler?.isSampleTestSet == true)
            println(outputString)
        }
    }

    private fun checkOutput(
        output: String,
        isPossible: Boolean,
        expectedOutput: String?,
        @Suppress("SameParameterValue") displayError: Boolean = false
    ) {
        try {
            val outputSplit = output.split(":")
            val outputTower = outputSplit[1].trim()
            if (!isPossible) {
                try {
                    expectedOutput?.checkExpectedOutput(output, true)
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

    private fun validateTower(tower: String, desiredLetter: Char): ValidateResult {
        val desiredLetterCount = tower.filter { it == desiredLetter }.length
        if (desiredLetterCount == tower.length) {
            return ValidateResult(true, ValidType.BOTH_ENDS)
        }

        val validFromFront: ValidType
        val testValue = when (desiredLetter) {
            tower.first() -> {
                validFromFront = ValidType.FROM_FRONT
                tower
            }
            tower.last() -> {
                validFromFront = ValidType.FROM_END
                tower.reversed()
            }
            else -> {
                validFromFront = ValidType.MIDDLE_ONLY
                tower.dropWhile { it != desiredLetter }
            }
        }
        val consecutiveDesiredLetters = testValue.takeWhile { it == desiredLetter }.length
        return ValidateResult(consecutiveDesiredLetters == desiredLetterCount, validFromFront)
    }

    data class ValidateResult(
        val isValid: Boolean,
        val validType: ValidType
    ) {
        val validFromFront = validType == ValidType.FROM_FRONT
    }

    enum class ValidType { FROM_FRONT, MIDDLE_ONLY, FROM_END, BOTH_ENDS }

    class FinalTower {
        private val partialTowers = mutableListOf<PartialTower>()

        /**
         * @return true if the tower is valid
         */
        fun addTowers(
            @Suppress("UNUSED_PARAMETER") towerLetter: Char, // Used for setting conditional breakpoints :P
            validFromEnd: Int? = null,
            repeatedLetter: List<Int> = listOf(),
            validFromFront: Int? = null
        ): Boolean {
            val towerSequence = repeatedLetter.toMutableList()
            if (validFromEnd != null) {
                if (towerSequence.isEmpty()) {
                    towerSequence.add(validFromEnd)
                }
                else {
                    towerSequence.add(0, validFromEnd)
                }
            }
            if (validFromFront != null) {
                towerSequence.add(validFromFront)
            }
            if (towerSequence.isEmpty()) {
                return true
            }

            val partialTower = PartialTower(towerSequence)
            val matchingLast = partialTowers.find { it.firstTowerIndex == partialTower.lastTowerIndex }
            val matchingFirst = partialTowers.find { it.lastTowerIndex == partialTower.firstTowerIndex }
            return when {
                matchingFirst == null && matchingLast == null -> {
                    if (towerSequence.size == 1
                            && partialTowers.filter { it.contains(towerSequence.first()) }.size == 1
                    ) {
                        return true
                    }
                    partialTowers.add(partialTower)
                    true
                }
                matchingFirst == matchingLast -> towerSequence.size == 1
                matchingFirst != null && matchingLast != null -> {
                    partialTowers.remove(matchingFirst)
                    partialTowers.remove(matchingLast)
                    partialTowers.add(matchingFirst.combineTowers(partialTower).combineTowers(matchingLast))
                    true
                }
                else -> {
                    val matchingTower = matchingFirst ?: matchingLast!!
                    partialTowers.remove(matchingTower)
                    partialTowers.add(matchingTower.combineTowers(partialTower))
                    true
                }
            }
        }

        override fun toString(): String {
            return partialTowers.joinToString("")
        }

        data class PartialTower(val towerIndexes: List<Int>) {
            val firstTowerIndex = towerIndexes.first()
            val lastTowerIndex = towerIndexes.last()

            fun combineTowers(other: PartialTower): PartialTower {
                if (towerIndexes.first() == other.towerIndexes.last()) {
                    return PartialTower(other.towerIndexes.dropLast(1).plus(towerIndexes))
                }
                if (towerIndexes.last() == other.towerIndexes.first()) {
                    return PartialTower(towerIndexes.dropLast(1).plus(other.towerIndexes))
                }
                if (towerIndexes.isEmpty()) {
                    return other
                }
                if (other.towerIndexes.isEmpty()) {
                    return this
                }
                if (other.towerIndexes.size == 1 && towerIndexes.contains(other.towerIndexes.first())) {
                    return this
                }
                if (towerIndexes.size == 1 && other.towerIndexes.contains(towerIndexes.first())) {
                    return other
                }
                throw IllegalStateException("Cannot combine towers")
            }

            override fun toString(): String {
                return towerIndexes.joinToString("") { towersInput[it] }
            }

            fun contains(index: Int) = towerIndexes.contains(index)
        }
    }
}