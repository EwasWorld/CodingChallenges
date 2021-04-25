#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
#parse("File Header.java")
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
val input = Scanner(File("${DIR_PATH}/testInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(nextLine())
    for (caseNumber in 1..totalCases) {
    
        println("Case #${DS}caseNumber: ")
    }
}

/**
 * Reads the next line from [input]. Terminates if a line reading 'end' is read. Prints lines beginning with '-----' and
 * then skips them
 * @return the next line from [input]
 */
private fun nextLine(): String {
    var line: String
    while (true) {
        line = input.nextLine()
        if (line.startsWith("-----")) {
            println(line)
            continue
        }
        if (line == "end") {
            println("End of inputs, exiting")
            exitProcess(0)
        }
        break
    }
    return line
}
