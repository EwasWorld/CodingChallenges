package googleCodeJam.mar2022.qualifying.problem4

import java.io.File
import java.util.*

fun main() {
//    val input = Scanner(System.`in`)
//    val input = Scanner(File("src\\googleCodeJam\\mar2022\\qualifying\\problem4\\testInput.txt"))
    val folder: String
    val fileNamePrefix: String
    if (true) {
        folder = "src\\googleCodeJam\\mar2022\\qualifying\\problem4\\test_set_1\\"
        fileNamePrefix = "ts1"
    }
    else {
        folder = "src\\googleCodeJam\\mar2022\\qualifying\\problem4\\sample_test_set_1\\"
        fileNamePrefix = "sample_ts1"
    }
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
            check(remainingRuns.none { it.sortedUnused.isEmpty() }) { "Empty run" }

            // Trigger any runs that only have one unused module in them
            val runsWithSingleUnusedModule = remainingRuns.filter { it.sortedUnused.size == 1 }
            if (runsWithSingleUnusedModule.isNotEmpty()) {
                runsWithSingleUnusedModule.forEach {
                    finalFunSum += it.useRun()
                    remainingRuns.remove(it)
                }
                continue
            }

            var runToSortedUnusedFun = remainingRuns.map { run -> RunToFun(run, run.sortedUnused.map { it.funFactor }) }

            val maxFunFactor = runToSortedUnusedFun.map { it.funFactors.first() }.max()!!
            runToSortedUnusedFun = runToSortedUnusedFun.filter { it.funFactors.first() == maxFunFactor }

            var runToTrigger: Run
            var idx = 1
            while (true) {
                val minFunFactor = runToSortedUnusedFun.map { it.funFactors[idx] }.min()!!
                runToSortedUnusedFun = runToSortedUnusedFun.filter { it.funFactors[idx] == minFunFactor }

                if (runToSortedUnusedFun.size == 1) {
                    runToTrigger = runToSortedUnusedFun.first().run
                    break
                }
                val maxLenRun = runToSortedUnusedFun.find { it.funFactors.size == idx + 1 }
                if (maxLenRun != null) {
                    runToTrigger = maxLenRun.run
                    break
                }

                idx++
            }
            runToTrigger.let {
                finalFunSum += it.useRun()
                remainingRuns.remove(it)
            }
            continue
        }

        val output = "Case #$testCaseIndex: $finalFunSum"
        val expectedLine = expectedOutput.nextLine()
        check(output == expectedLine) { "Failed: Wrong answer" }
        println(output)
    }
    println("All passed")
}

data class RunToFun(
    val run: Run,
    val funFactors: List<Int>
)

data class Module(
    val funFactor: Int,
    var nextModule: Module? = null,
    var previousModules: MutableList<Module> = mutableListOf(),
    var isUsed: Boolean = false,
    var runs: MutableList<Run> = mutableListOf()
) {
    val isStartModule: Boolean
        get() = previousModules.isEmpty()

    override fun toString(): String {
        return funFactor.toString()
    }
}

data class Run(
    val id: Int,
    val modules: MutableList<Module> = mutableListOf(),
    var isComplete: Boolean = false
) {
    lateinit var sortedUnused: MutableList<Module>

    fun finalise() {
        sortedUnused = modules.sortedByDescending { it.funFactor }.toMutableList()
    }

    override fun toString(): String {
        return id.toString() + ": " + modules.joinToString(" ") { it.funFactor.toString() }
    }

    fun useRun(): Int {
        check(!isComplete) { "Run completed twice" }

        val funFactorToUse = modules.maxBy {
            if (it.isUsed) -1 else it.funFactor
        }!!.funFactor
        check(funFactorToUse != -1)

        val moduleToUse = modules.first { !it.isUsed && it.funFactor == funFactorToUse }
        moduleToUse.runs.forEach {
            it.sortedUnused.remove(moduleToUse)
        }

        moduleToUse.isUsed = true
        isComplete = true

        return funFactorToUse
    }
}