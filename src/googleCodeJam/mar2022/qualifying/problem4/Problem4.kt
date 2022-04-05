package googleCodeJam.mar2022.qualifying.problem4

import java.io.File
import java.util.*

fun main() {
//    val input = Scanner(System.`in`)
//    val input = Scanner(File("src\\googleCodeJam\\mar2022\\qualifying\\problem4\\testInput.txt"))
    val folder: String
    val fileNamePrefix: String
    if (true) {
        val testSet = "1"
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
    val testCases = input.nextLine().toInt()
    for (testCaseIndex in 1..testCases) {
        val totalModules = input.nextLine().toInt()
        val modules = input.nextLine().split(" ")
                .map { Module(it.toInt()) }
                .toMutableList()
        check(modules.size == totalModules) { "Bad input" }

        input.nextLine().split(" ")
                .takeIf { it.size == totalModules }!!
                .forEachIndexed { moduleIndex, nextModuleIndexString ->
                    nextModuleIndexString.toInt()
                            .takeIf { it > 0 }?.minus(1)
                            ?.let { nextModuleIndex -> modules[moduleIndex].nextModule = modules[nextModuleIndex] }
                }
        modules.sortByDescending { it.funFactor }

        val remainingChains = modules.mapIndexedNotNull { moduleIndex, module ->
            if (!module.isStartModule) return@mapIndexedNotNull null

            val chain = ChainReaction(moduleIndex)

            val chainModules = mutableListOf<Module>()
            module.forEachTriggerableInChain {
                chainModules.add(it)
                it.untriggeredChains.add(chain)
            }

            chain.modules = chainModules
            chain
        }.toMutableList()

        var finalFunSum = 0L
        while (remainingChains.isNotEmpty()) {
            check(remainingChains.none { it.sortedTriggerableModules.isEmpty() }) { "Empty chain" }

            // Trigger any chains that only have one unused module in them
            val chainsWithSingleUnusedModule = remainingChains.filter { it.sortedTriggerableModules.size == 1 }
                    .takeIf { it.isNotEmpty() }
            if (chainsWithSingleUnusedModule != null) {
                chainsWithSingleUnusedModule.forEach {
                    finalFunSum += it.trigger()
                    remainingChains.remove(it)
                }
                continue
            }

            // Find the max funFactor of the chain whose highest funFactor is minimal
            val nextFunFactorToTrigger = remainingChains.map { it.sortedTriggerableModules.first().funFactor }.getMin()
            // Find the module with the desired fun factor that is closest to the end of a chain
            // (allows all duplicates to be triggered to, rather than the first chain potentially triggering them all)
            val nextModuleToTrigger = modules
                    .filter { !it.hasBeenTriggered && it.funFactor == nextFunFactorToTrigger }
                    .map {
                        var distanceToEnd = 0
                        it.forEachTriggerableInChain { distanceToEnd++ }
                        it to distanceToEnd
                    }
                    .getMinBy { it.second }
                    .first
            // Find all chains for which the highest fun factor is the module we want to trigger
            //     and prepare them for analysis
            var consideredChains = remainingChains.filter { it.sortedTriggerableModules.contains(nextModuleToTrigger) }
                    .map { chain -> ChainToFunList(chain, chain.sortedTriggerableModules.map { it.funFactor }) }

            var chainReactionToTrigger: ChainReaction
            var idx = 1
            while (true) {
                // Find the minimum next-highest funFactor
                val minFunFactor = consideredChains.map { it.funFactors[idx] }.getMin()
                // Find all chains with the minimal next-highest funFactor
                consideredChains = consideredChains.filter { it.funFactors[idx] == minFunFactor }

                if (consideredChains.size == 1) {
                    chainReactionToTrigger = consideredChains.first().chainReaction
                    break
                }
                // If any chains have no more modules, trigger one of these at random
                // (all are identical in their descending list of funFactors)
                val maxLenChain = consideredChains.find { it.funFactors.size == idx + 1 }
                if (maxLenChain != null) {
                    chainReactionToTrigger = maxLenChain.chainReaction
                    break
                }

                idx++
            }

            chainReactionToTrigger.let {
                finalFunSum += it.trigger()
                remainingChains.remove(it)
            }
            continue
        }

        val output = "Case #$testCaseIndex: $finalFunSum"
        val expectedLine = expectedOutput?.nextLine()
        check(expectedLine == null || output == expectedLine) { "Failed: Wrong answer" }
        println(output)
    }
}

data class ChainToFunList(
    val chainReaction: ChainReaction,
    val funFactors: List<Int>
)

data class Module(
    val funFactor: Int
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
    var modules: List<Module> = listOf()
        set(value) {
            field = value
            sortedTriggerableModules = value.sortedByDescending { it.funFactor }
        }

    /**
     * Whether this chain has been triggered
     */
    private var hasBeenTriggered: Boolean = false

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
    fun trigger(): Int {
        check(!hasBeenTriggered) { "Chain completed twice" }

        val triggeredModules = getModulesThatWillTrigger()
        val funFactorToUse = triggeredModules.map { it.funFactor }.getMax()
        check(funFactorToUse != -1)

        val affectedChains = mutableListOf<ChainReaction>()
        triggeredModules.forEach {
            it.hasBeenTriggered = true
            affectedChains.addAll(it.untriggeredChains)
        }
        affectedChains.distinctBy { it.id }.forEach { chain ->
            chain.sortedTriggerableModules = chain.getModulesThatWillTrigger().sortedByDescending { it.funFactor }
        }

        hasBeenTriggered = true
        sortedTriggerableModules = mutableListOf()

        return funFactorToUse
    }
}

/**
 * Custom min function because apparently Google doesn't understand that Kotlin has a built in function for this
 */
fun List<Int>.getMin(): Int {
    var min = Int.MAX_VALUE
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
fun List<Int>.getMax(): Int {
    var max = Int.MIN_VALUE
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
fun <T> List<T>.getMinBy(selector: (T) -> Int): T {
    require(isNotEmpty()) { "List is empty" }

    var minValue = Int.MAX_VALUE
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