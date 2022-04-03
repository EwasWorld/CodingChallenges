package googleCodeJam.mar2022.qualifying.problem4

import java.io.File
import java.util.*

fun main() {
//    val input = Scanner(System.`in`)
    val folder = "src\\googleCodeJam\\mar2022\\qualifying\\problem4\\sample_test_set_1\\"
    val fileNamePrefix = "sample_ts1"
//    val input = Scanner(File("src\\googleCodeJam\\mar2022\\qualifying\\problem4\\testInput.txt"))
    val input = Scanner(File("$folder${fileNamePrefix}_input.txt"))
    val expectedOutput = Scanner(File("$folder${fileNamePrefix}_output.txt"))
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
        val totalModules = input.nextLine().toInt()
        val modules = input.nextLine().split(" ")
                .map { Module(it.toInt()) }
                .toMutableList()
        check(modules.size == totalModules) { "Bad input" }

        input.nextLine().split(" ")
                .takeIf { it.size == totalModules }!!
                .forEachIndexed { index, str ->
                    val connection = str.toInt().takeIf { it > 0 }?.minus(1) ?: return@forEachIndexed
                    modules[index].nextModule = modules[connection]
                    modules[connection].previousModules.add(modules[index])
                }
        modules.sortByDescending { it.funFactor }

        @Suppress("USELESS_CAST") val remainingRuns = modules.mapIndexedNotNull { index, module ->
            if (!module.isStartModule) return@mapIndexedNotNull null
            val run = Run(index)
            var current: Module? = module
            do {
                run.modules.add(current!!)
                current.runs.add(run)

                current = current.nextModule
            } while (current != null)
            run.finalise()
            run
        }.toMutableList()

        var finalFunSum = 0L
        while (remainingRuns.isNotEmpty()) {
            val funFactorToUse = modules.first { !it.isUsed }.funFactor
            val modulesToUse = modules.filter { it.funFactor == funFactorToUse }
            var consideredRuns = modulesToUse
                    .asSequence()
                    .map { it.runs }
                    .flatten()
                    .distinctBy { it.id }
                    .filter { !it.isComplete }
                    .map { it to it.findNextHighest(funFactorToUse) }
                    .sortedBy { it.second.first() }
                    .toList()

            var index = 0
            var runToTrigger: Run
            while (true) {
                consideredRuns = consideredRuns.filter {
                    it.second[index] == consideredRuns[0].second[index]
                }
                if (consideredRuns.size == 1) {
                    runToTrigger= consideredRuns.first().first
                    break
                }
                val maxLenRun = consideredRuns.find { it.second.size == index + 1 }
                if (maxLenRun != null) {
                    runToTrigger= maxLenRun.first
                    break
                }

                consideredRuns = consideredRuns.sortedBy { it.second[index + 1] }
                index++
            }

            finalFunSum += runToTrigger.useRun()
            remainingRuns.removeIf { it.id == runToTrigger.id }
        }

        val output = "Case #$testCaseIndex: $finalFunSum"
        println(output)
        val expectedLine = expectedOutput.nextLine()
        check(output == expectedLine) { "Failed: Wrong answer" }
//        check(output == expectedLine) { "Expected: $expectedLine\nActual: $output" }
    }
    println("All passed")
}

data class Module(
    val funFactor: Int,
    var nextModule: Module? = null,
    var previousModules: MutableList<Module> = mutableListOf(),
    var isUsed: Boolean = false,
    var runs: MutableList<Run> = mutableListOf()
) {
    val isStartModule: Boolean
        get() = previousModules.isEmpty()
}

data class Run(
    val id: Int,
    val modules: MutableList<Module> = mutableListOf(),
    var isComplete: Boolean = false
) {
    private var sorted: List<Module>? = null

    fun finalise() {
        check(sorted == null) { "Already finalised" }
        sorted = modules.sortedByDescending { it.funFactor }
    }

    override fun toString(): String {
        return id.toString() + ": " + modules.joinToString(" ") { it.funFactor.toString() }
    }

    fun findNextHighest(funFactor: Int): List<Int> {
        return sorted!!
                .filter { !it.isUsed && it.funFactor <= funFactor }
                .takeIf { it.size > 1 }
                ?.drop(1)
                ?.map { it.funFactor }
                ?: listOf(-1)
    }

    fun useRun(): Int {
        check(!isComplete) { "Run completed twice" }

        val funFactorToUse = modules.maxBy {
            if (it.isUsed) -1 else it.funFactor
        }!!.funFactor
        check(funFactorToUse != -1)

        modules.first { !it.isUsed && it.funFactor == funFactorToUse }.isUsed = true
        isComplete = true

        return funFactorToUse
    }
}