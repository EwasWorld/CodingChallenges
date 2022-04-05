package googleCodeJam.mar2022.qualifying.problem4

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
            check(remainingRuns.none { it.sortedTriggerModules.isEmpty() }) { "Empty run" }

            // Trigger any runs that only have one unused module in them
            val runsWithSingleUnusedModule = remainingRuns.filter { it.sortedTriggerModules.size == 1 }
            if (runsWithSingleUnusedModule.isNotEmpty()) {
                runsWithSingleUnusedModule.forEach {
                    finalFunSum += it.useRun()
                    remainingRuns.remove(it)
                }
                continue
            }

            val nextFunFactorToTrigger = remainingRuns.map { it.sortedTriggerModules.first().funFactor }.min()!!
            // Find the module with the desired fun factor that is closest to the end of a chain
            // (allows all duplicates to be triggered to, rather than the first run potentially triggering all of them)
            val nextModuleToTrigger = modules
                    .filter { !it.isUsed && it.funFactor == nextFunFactorToTrigger }
                    .map {
                        var distanceToEnd = 0
                        var currentModule = it
                        while (currentModule.nextModule != null && !currentModule.nextModule!!.isUsed) {
                            currentModule = currentModule.nextModule!!
                            distanceToEnd++
                        }
                        it to distanceToEnd
                    }
                    .minBy { it.second }!!
                    .first
            var consideredRuns = remainingRuns.filter { it.sortedTriggerModules.contains(nextModuleToTrigger) }
                    .map { run -> RunToFun(run, run.sortedTriggerModules.map { it.funFactor }) }

            var runToTrigger: Run
            var idx = 1
            while (true) {
                val minFunFactor = consideredRuns.map { it.funFactors[idx] }.min()!!
                consideredRuns = consideredRuns.filter { it.funFactors[idx] == minFunFactor }

                if (consideredRuns.size == 1) {
                    runToTrigger = consideredRuns.first().run
                    break
                }
                val maxLenRun = consideredRuns.find { it.funFactors.size == idx + 1 }
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

    var numberOfModulesAfterThisOne: Int? = null

    override fun toString(): String {
        return funFactor.toString()
    }

    fun updateModulesToEnd() {

    }
}

data class Run(
    val id: Int,
    val modules: MutableList<Module> = mutableListOf(),
    var isComplete: Boolean = false
) {
    lateinit var sortedTriggerModules: MutableList<Module>

    fun finalise() {
        sortedTriggerModules = modules.sortedByDescending { it.funFactor }.toMutableList()
    }

    fun updateSortedUnused() {
        sortedTriggerModules = getModulesThatWillTrigger().sortedByDescending { it.funFactor }.toMutableList()
    }

    override fun toString(): String {
        return id.toString() + ": " + modules.joinToString(" ") { it.funFactor.toString() }
    }

    fun getModulesThatWillTrigger() = modules.takeWhile { !it.isUsed }

    /**
     * Sets [isComplete] to true, [Module.isUsed] as appropriate on the modules that will be triggered
     */
    fun useRun(): Int {
        check(!isComplete) { "Run completed twice" }

        val triggeredModules = getModulesThatWillTrigger()
        val funFactorToUse = triggeredModules.map { it.funFactor }.max()!!
        check(funFactorToUse != -1)

        val affectedRuns = mutableListOf<Run>()
        triggeredModules.forEach {
            it.isUsed = true
            affectedRuns.addAll(it.runs)
        }
        affectedRuns.distinctBy { it.id }.forEach {
            it.updateSortedUnused()
        }

        isComplete = true
        sortedTriggerModules = mutableListOf()

        return funFactorToUse
    }
}