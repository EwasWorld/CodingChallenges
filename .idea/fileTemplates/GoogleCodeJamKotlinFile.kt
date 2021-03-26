#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
#parse("File Header.java")
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
val input = Scanner(File("${DIR_PATH}/testInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(googleCodeJam.mar2021.qualifying.problem2.input.nextLine())
    for (caseNumber in 1..totalCases) {
    
        println("Case #${DS}caseNumber: ")
    }
}