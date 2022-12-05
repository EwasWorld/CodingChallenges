package googleCodeJam.mar2022

import googleCodeJam.mar2022.IoHandler.Companion.checkExpectedOutput
import java.util.*


fun main() {
    val ioHandler = IoHandler(
            "src/googleCodeJam/mar2022/round/problem/test_data",
            1,
            true
    )
    Problem.execute(ioHandler)
}

object Problem {
    fun execute(ioHandler: IoHandler? = null) {
        val input = ioHandler?.input ?: Scanner(System.`in`)
        val output = ioHandler?.expectedOutput

        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedOutput = output?.nextLine()


            val outputString = "Case #$testCaseIndex: "
            expectedOutput?.checkExpectedOutput(outputString, true)
            println(outputString)
        }
    }
}