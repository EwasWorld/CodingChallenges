package googleCodeJam.mar2022.qualifying.problem4

import googleCodeJam.mar2022.qualifying.TimeChecker
import java.util.*

/*
NOTES:

`list.map { something }.min()` and `list.minBy { something }.something` take about the same time

 */

fun main() {
    val chains = List(10000) {
        generateChain()
    }

    val timeChecker = TimeChecker()
    chains.map { it.sortedTriggerableModules.first().funFactor }.getMin()
    timeChecker.logTime("1")
    chains.getMinBy { it.sortedTriggerableModules.first().funFactor }.sortedTriggerableModules.first().funFactor
    timeChecker.logTime("2")


    return
}

fun generateChain(funFactor: Int? = null): ChainReaction {
    var prevModule: Module? = null
    val chainModules = List(10000) {
        val newModule = Module(funFactor ?: Random().nextInt(500))
        if (prevModule != null) newModule.nextModule = prevModule
        prevModule = newModule
        newModule
    }
    return ChainReaction(1).apply { modules = chainModules }
}