package googleCodeJam.mar2021.qualifying.problem5

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.*
import kotlin.math.*

private const val pathPrefix = "src/googleCodeJam/mar2021/qualifying/problem5/"

lateinit var groupedQuestions: Map<Double, List<Question>>

@Suppress("unused")
val stdIoScanner = Scanner(BufferedReader(InputStreamReader(System.`in`)))
@Suppress("unused")
val testInputScanner = Scanner(File("${pathPrefix}testInput.txt"))
const val minDifficulty = -3
const val maxDifficulty = 3
const val difficultyRange = maxDifficulty - minDifficulty

/**
 * @return that the player will answer a question correctly given they have skill level [playerSkill] and the question
 * has difficulty [questionDifficulty]
 */
private fun probabilityCorrect(playerSkill: Double, questionDifficulty: Double): Double {
    return 1.0 / (1 + exp(-(playerSkill - questionDifficulty)))
}

fun main() {
//    CustomParameters.generateNew(100, 100)
    val params = CustomParameters()
//    val params = ProblemParameters(testInputScanner)

    /*
     * Adjusters
     */
    val granularity = params.totalCases / (params.totalCases / 10.0)

    for (caseNumber in 1..params.totalCases) {
        var allPlayers = params.getNextTestCaseInfo()

        // item i is the number of correct answers for question i
        val questionsResults = IntArray(params.questionCount) { questionNumber ->
            allPlayers.map { player -> player.answers[questionNumber] }.sum()
        }
        val questions =
                questionsResults.withIndex().sortedByDescending { it.value }.mapIndexed { sortIndex, questionInfo ->
                    Question(
                            questionInfo.index,
                            minDifficulty + sortIndex * (difficultyRange / params.questionCount.toDouble()),
                            questionInfo.value
                    )
                }

        val groupingBinSize = difficultyRange / granularity.toDouble()
        groupedQuestions = questions.groupBy {
            (floor((it.estimatedDifficulty - minDifficulty) / groupingBinSize) * groupingBinSize
                    + minDifficulty + (groupingBinSize / 2))
        }

        allPlayers = allPlayers.sortedBy { it.answers.sum() }
        allPlayers.sortedBy { it.answers.sum() }.forEachIndexed { index, player ->
            player.calculate(minDifficulty + index * difficultyRange / params.playerCount.toDouble())
        }

        allPlayers = allPlayers.sortedBy { it.differenceInNumberOfAns }
        val top5 = allPlayers.take(10)
        val bottom5 = allPlayers.takeLast(10)
        val player59Index = allPlayers.indexOfFirst { it.playerNumber == 59 }

        println("Case #$caseNumber: ")
    }
}

data class Question(val questionNumber: Int, val estimatedDifficulty: Double, val totalCorrectAnswers: Int)

open class Player(val playerNumber: Int, val answers: List<Int>) {
    var totalDifference = 0.0
    var differenceInNumberOfAns = 0.0

    fun calculate(estimatedSkill: Double) {
        totalDifference = 0.0
        for (questionGroup in groupedQuestions) {
            val questionNumbers = questionGroup.value.map { it.questionNumber }
            val actualPercentCorrect = answers.slice(questionNumbers).sum() / questionNumbers.size.toDouble()
            totalDifference += abs(probabilityCorrect(estimatedSkill, questionGroup.key) - actualPercentCorrect)

            for (question in questionGroup.value) {
                val value = answers[question.questionNumber] - probabilityCorrect(
                        estimatedSkill, question.estimatedDifficulty
                )
                differenceInNumberOfAns += value * value
            }
        }
    }
}


open class ProblemParameters() {
    companion object {
        fun parseAnswerString(answers: String): List<Int> {
            return answers.toCharArray().map { Integer.parseInt(it.toString()) }
        }
    }

    var input: Scanner? = null
        protected set
    var totalCases = -1
        protected set
    var playerCount = 100
        protected set
    var questionCount = 10000
        protected set

    @Suppress("unused")
    constructor(input: Scanner): this() {
        this.input = input
        totalCases = Integer.parseInt(input.nextLine())
        // Skip past the percentage of cases that must be correct
        input.nextLine()
    }

    open fun getNextTestCaseInfo(): List<Player> {
        return List(playerCount) { playerNumber ->
            Player(playerNumber, parseAnswerString(input!!.nextLine()))
        }
    }
}

/**
 * A custom parameter generator/parser which gives extra information than the actual problem would to facilitate testing
 */
@Suppress("unused")
class CustomParameters : ProblemParameters() {
    companion object {
        val file = File("${pathPrefix}generatedInput")
        const val delimiter = ","

        /**
         * Generates a single test set using the given information and writes it to [file]. Skill/difficulty levels
         * increase evenly the higher the player/question number. Cheater is always the first player and they will cheat
         * on every even question. Note extra information is written to the file than the actual problem would give,
         * this is to allow for testing
         */
        fun generateNew(playerCount: Int, questionCount: Int) {
            /*
             * Generate data
             */
            val questionDifficulties = List(questionCount) { index -> generateSkillLevel(index, questionCount) }
            val players = List(playerCount) { playerNumber ->
                val skill = generateSkillLevel(playerNumber, playerCount)
                var answers = questionDifficulties.map { difficulty ->
                    if (Random().nextDouble() > probabilityCorrect(skill, difficulty)) 1 else 0
                }
                if (playerNumber == 0) {
                    answers = answers.mapIndexed { index, value -> if (index % 2 == 0) 1 else value }
                }
                PlayerFullInfo(playerNumber, skill, answers)
            }

            /*
             * Write data
             */
            val stringBuilder = StringBuilder()
            stringBuilder.append(playerCount.toString())
            stringBuilder.append("\n")
            stringBuilder.append(questionCount.toString())
            stringBuilder.append("\n")
            stringBuilder.append(questionDifficulties.joinToString(delimiter))
            stringBuilder.append("\n")
            for (player in players) {
                stringBuilder.append(player.toString())
                stringBuilder.append("\n")
            }
            file.writeText(stringBuilder.toString())
        }

        private fun generateSkillLevel(index: Int, totalItems: Int): Double {
            val multiplier = 10.0.pow(3)
            return round((minDifficulty + index * difficultyRange / totalItems.toDouble()) * multiplier) / multiplier
        }
    }

    val questionDifficulties: List<Double>
    var playerFullInfo = listOf<PlayerFullInfo>()

    init {
        val input = Scanner(file)
        this.input = input
        this.playerCount = Integer.parseInt(input.nextLine())
        this.questionCount = Integer.parseInt(input.nextLine())
        totalCases = 1
        this.questionDifficulties = input.nextLine().split(delimiter).map { it.toDouble() }
    }

    override fun getNextTestCaseInfo(): List<Player> {
        playerFullInfo = MutableList(playerCount) { PlayerFullInfo.fromString(input!!.nextLine()) }
        return playerFullInfo
    }

    class PlayerFullInfo(playerNumber: Int, val skillLevel: Double, answers: List<Int>): Player(playerNumber, answers) {
        override fun toString(): String {
            return playerNumber.toString() + delimiter + skillLevel + delimiter + answers.joinToString("")
        }

        companion object {
            fun fromString(string: String): PlayerFullInfo {
                val split = string.split(delimiter)
                return PlayerFullInfo(split[0].toInt(), split[1].toDouble(), parseAnswerString(split[2]))
            }
        }
    }
}
