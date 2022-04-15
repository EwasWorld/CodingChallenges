package googleCodeJam.mar2022.qualifying.problem4

import googleCodeJam.mar2022.qualifying.TimeChecker
import googleCodeJam.mar2022.qualifying.problem4.Problem4.getMin
import googleCodeJam.mar2022.qualifying.problem4.Problem4.getMinBy
import java.util.*

/*
NOTES:

`list.map { something }.min()` and `list.minBy { something }.something` take about the same time

 */

fun main() {
    val chains = List(10000) {
        Problem4SpeedTests.generateChain()
    }

    val timeChecker = TimeChecker()
    chains.map { it.sortedTriggerableModules.first().funFactor }.getMin()
    timeChecker.logTime("1")
    chains.getMinBy { it.sortedTriggerableModules.first().funFactor }.sortedTriggerableModules.first().funFactor
    timeChecker.logTime("2")


    return
}

object Problem4SpeedTests {
    fun generateChain(funFactor: Long? = null): Problem4.ChainReaction {
        var prevModule: Problem4.Module? = null
        val chainModules = List(10000) {
            val newModule = Problem4.Module(funFactor ?: Random().nextInt(500).toLong())
            if (prevModule != null) newModule.nextModule = prevModule
            prevModule = newModule
            newModule
        }
        return Problem4.ChainReaction(1).apply {
            setModulesAndFinalise(chainModules)
        }
    }
}