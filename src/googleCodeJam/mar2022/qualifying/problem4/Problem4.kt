package googleCodeJam.mar2022.qualifying.problem4

import googleCodeJam.mar2022.qualifying.TimeAccumulator
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

    execute(input, expectedOutput)

    println("All passed")
}

fun execute(input: Scanner, expectedOutput: Scanner?) {
    val timeChecker = TimeAccumulator()
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
//        if (testCaseIndex > 20) break
        val expectedLine = expectedOutput?.nextLine()
        val totalModules = input.nextLine().toInt()
        val modules = input.nextLine().split(" ")
                .takeIf { it.size == totalModules }!!
                .map { Module(it.toLong()) }
                .toMutableList()

        input.nextLine().split(" ")
                .takeIf { it.size == totalModules }!!
                .forEachIndexed { moduleIndex, nextModuleIndexString ->
                    nextModuleIndexString.toInt()
                            .takeIf { it > 0 }?.minus(1)
                            ?.let { nextModuleIndex -> modules[moduleIndex].nextModule = modules[nextModuleIndex] }
                }
        modules.sortByDescending { it.funFactor }

//        if (testCaseIndex != 26) {
//            println("Skipped $testCaseIndex")
//            continue
//        }

        timeChecker.logTime(0)

        var finalFunSum = 0L
        val remainingChains = modules.mapIndexedNotNull { moduleIndex, module ->
            if (!module.isStartModule) return@mapIndexedNotNull null

            val chain = ChainReaction(moduleIndex)

            val chainModules = mutableListOf<Module>()
            module.forEachTriggerableInChain {
                chainModules.add(it)
                it.untriggeredChains.add(chain)
            }

            chain.takeIf {
                val triggerResult = it.setModulesAndFinalise(chainModules)
                        ?: return@takeIf true
                finalFunSum += triggerResult.sum
                false
            }
        }.toMutableList()

        val groupedModules = modules.groupBy { it.funFactor }

        timeChecker.logTime(1)
        while (remainingChains.isNotEmpty()) {
            remainingChains.sortBy { it.sortedTriggerableModules.first().funFactor }

            // Find the max funFactor of the chain whose highest funFactor is minimal
            val nextFunFactorToTrigger = remainingChains.first().sortedTriggerableModules.first().funFactor
            // Find the module with the desired fun factor that is closest to the end of a chain
            // (allows all duplicates to be triggered to, rather than the first chain potentially triggering them all)
            val nextModuleToTrigger = groupedModules[nextFunFactorToTrigger]!!
                    .filterNot { it.hasBeenTriggered }
                    .let {
                        if (it.size == 1) return@let it.first()
                        var lowest: Pair<Module, Int>? = null
                        it.forEach { module ->
                            val modulesInChain = module.untriggeredChains.first().modules
                            val distanceToEnd = modulesInChain.size - modulesInChain.indexOf(module)
                            if (lowest == null || lowest!!.second > distanceToEnd) {
                                lowest = module to distanceToEnd
                            }
                        }
                        lowest!!.first
                    }
            // Find all chains for which the highest fun factor is the module we want to trigger
            //     and prepare them for analysis
            var consideredChains = nextModuleToTrigger.untriggeredChains
                    .map { chain -> ChainToFunList(chain, chain.sortedTriggerableModules.map { it.funFactor }) }

            timeChecker.logTime(2)
            var chainReactionToTrigger: ChainReaction
            var checkIndex = 0
            while (true) {
                if (consideredChains.size == 1) {
                    chainReactionToTrigger = consideredChains.first().chainReaction
                    break
                }
                // If any chains have no more modules, trigger one of these at random
                // (all are identical in their descending list of funFactors)
                val maxLenChain = consideredChains.find { it.funFactors.size == checkIndex + 1 }
                if (maxLenChain != null) {
                    chainReactionToTrigger = maxLenChain.chainReaction
                    break
                }

                checkIndex++

                // Find the minimum next-highest funFactor
                val minFunFactor = consideredChains.map { it.funFactors[checkIndex] }.getMin()
                // Find all chains with the minimal next-highest funFactor
                consideredChains = consideredChains.filter { it.funFactors[checkIndex] == minFunFactor }
            }

            chainReactionToTrigger.let {
                val triggerResult = it.trigger()
                finalFunSum += triggerResult.sum
                remainingChains.removeAll(triggerResult.chainsTriggered)
            }
            timeChecker.logTime(3)
        }

        val output = "Case #$testCaseIndex: $finalFunSum"
        check(expectedLine == null || output == expectedLine) { "Failed: Wrong answer" }
        println(output)
    }
    timeChecker.printFinal()
}

data class ChainToFunList(
    val chainReaction: ChainReaction,
    val funFactors: List<Long>
)

data class Module(
    val funFactor: Long
) {
    /**
     * Next module in the chain reaction
     */
    var nextModule: Module? = null
        set(value) {
            value!!.isStartModule = false
            field = value
        }

    /**
     * True if this module is not connected to by any other module
     */
    var isStartModule: Boolean = true
        private set

    /**
     * Has been triggered by the chain reaction
     */
    var hasBeenTriggered: Boolean = false

    /**
     * The untriggered chains this module is a part of
     */
    var untriggeredChains: MutableList<ChainReaction> = mutableListOf()

    /**
     * Defined to make the debug console easier to read
     */
    override fun toString(): String {
        return funFactor.toString()
    }

    /**
     * Iterate through all [Module]s in the [ChainReaction] as if this [Module] was being triggered
     * (stops when it reaches a module with [Module.hasBeenTriggered])
     */
    fun forEachTriggerableInChain(block: (Module) -> Unit) {
        var current: Module? = this
        do {
            block(current!!)
            current = current.nextModule
        } while (current != null && !current.hasBeenTriggered)
    }
}

data class ChainReaction(
    val id: Int
) {
    /**
     * [Module]s that make up this chain, in the order they will be triggered
     */
    lateinit var modules: List<Module>
        private set

    /**
     * @return the result of [ChainReaction.trigger] if the chain was auto-triggered due to having only 1 module
     */
    fun setModulesAndFinalise(value: List<Module>): TriggerResult? {
        check(value.isNotEmpty()) { "Empty list" }
        modules = value
        if (value.size == 1) {
            return trigger()
        }
        sortedTriggerableModules = value.sortedByDescending { it.funFactor }
        return null
    }

    /**
     * Whether this chain has been triggered
     */
    private var hasBeenTriggered: Boolean = false

    /**
     * This chain is about to be triggered.
     * Used to ensure when [ChainReaction.trigger] recurses, each [ChainReaction] is triggered only once
     * and that it happens as high up the call stack as possible.
     */
    private var markedForTrigger: Boolean = false

    /**
     * [Module]s that will be triggered, sorted descending by their [Module.funFactor]
     */
    lateinit var sortedTriggerableModules: List<Module>

    /**
     * Defined to make the debug console easier to read
     */
    override fun toString(): String {
        return id.toString() + ": " + modules.joinToString(" ") { it.funFactor.toString() }
    }

    private fun getModulesThatWillTrigger() = modules.takeWhile { !it.hasBeenTriggered }

    /**
     * Sets [hasBeenTriggered] to true, [Module.hasBeenTriggered] as appropriate on the modules that will be triggered
     */
    fun trigger(): TriggerResult {
        check(!hasBeenTriggered) { "Chain completed twice" }

        val triggeredModules = getModulesThatWillTrigger()
        var funFactorToUse = triggeredModules.map { it.funFactor }.getMax()
        check(funFactorToUse != -1L)

        val affectedChains = mutableSetOf<ChainReaction>()
        val chainsToTrigger = mutableSetOf<ChainReaction>()
        val triggeredChains = mutableSetOf(this)
        triggeredModules.forEach {
            it.hasBeenTriggered = true
            affectedChains.addAll(it.untriggeredChains)
        }
        affectedChains.minus(this).forEach { chain ->
            chain.sortedTriggerableModules = chain.getModulesThatWillTrigger().sortedByDescending { it.funFactor }
            if (!chain.markedForTrigger && !chain.hasBeenTriggered && chain.sortedTriggerableModules.size == 1) {
                chainsToTrigger.add(chain)
                chain.markedForTrigger = true
            }
        }
        chainsToTrigger.forEach { chain ->
            val result = chain.trigger()
            funFactorToUse += result.sum
            triggeredChains.addAll(result.chainsTriggered)
        }

        hasBeenTriggered = true
        sortedTriggerableModules = mutableListOf()

        return TriggerResult(funFactorToUse, triggeredChains)
    }
}

data class TriggerResult(
    val sum: Long,
    val chainsTriggered: Set<ChainReaction>
)

/**
 * Custom min function because apparently Google doesn't understand that Kotlin has a built in function for this
 */
fun List<Long>.getMin(): Long {
    var min = Long.MAX_VALUE
    forEach {
        if (it < min) {
            min = it
        }
    }
    return min
}

/**
 * Custom max function because apparently Google doesn't understand that Kotlin has a built in function for this
 */
fun List<Long>.getMax(): Long {
    var max = Long.MIN_VALUE
    forEach {
        if (it > max) {
            max = it
        }
    }
    return max
}

/**
 * Custom minBy function because apparently Google doesn't understand that Kotlin has a built in function for this
 */
fun <T> List<T>.getMinBy(selector: (T) -> Long): T {
    require(isNotEmpty()) { "List is empty" }

    var minValue = Long.MAX_VALUE
    var minItem: T? = null
    forEach {
        val value = selector(it)
        if (minItem == null || value < minValue) {
            minItem = it
            minValue = value
        }
    }
    return minItem!!
}