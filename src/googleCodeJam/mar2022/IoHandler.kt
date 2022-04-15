package googleCodeJam.mar2022

import java.io.File
import java.util.*

class IoHandler private constructor(
    useConsole: Boolean,
    testSetPrefix: String? = null,
    testSet: Int? = null,
    isSampleTestSet: Boolean? = null
) {
    constructor(
        testSetPrefix: String,
        testSet: Int = 1,
        isSampleTestSet: Boolean = true
    ) : this(false, testSetPrefix, testSet, isSampleTestSet)

    constructor() : this(true)

    val input: Scanner
    var expectedOutput: Scanner? = null

    init {
        if (useConsole) {
            input = Scanner(System.`in`)
        }
        else {
            val folder: String
            val fileNamePrefix: String
            if (isSampleTestSet!!) {
                folder = "$testSetPrefix\\sample_test_set_$testSet\\"
                fileNamePrefix = "sample_ts$testSet"
            }
            else {
                folder = "$testSetPrefix\\test_set_$testSet\\"
                fileNamePrefix = "ts$testSet"
            }
            input = Scanner(File("$folder${fileNamePrefix}_input.txt"))
            expectedOutput = Scanner(File("$folder${fileNamePrefix}_output.txt"))
        }
    }

    companion object {
        fun String.checkExpectedOutput(actual: String, printExpected: Boolean = false) {
            check (this == actual) {
                val expectedPrint = if (printExpected) ", Expected: $this" else ""
                "Wrong answer: Actual: $actual$expectedPrint"
            }
        }
    }
}