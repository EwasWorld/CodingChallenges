package googleCodeJam.mar2021.round1b.problem1

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.system.exitProcess

val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src/googleCodeJam/mar2021/round1b/problem1/testInput.txt"))
//val input = Scanner(File("src/googleCodeJam/mar2021/round1b/problem1/customInput.txt"))

/**
 * Total number of ticks on the clock face
 */
private const val maxTickSize = 360 * 12 * 1.0e10.toLong()
/**
 * Number of ticks the second hand moves every nanosecond
 */
private const val secondHandTicksInANano = 720L
/**
 * Number of ticks the minute hand moves every nanosecond
 */
private const val minuteHandTicksInANano = 12L //  720 / 12 = 60
/**
 * Number of ticks the hour hand moves every nanosecond
 */
private const val hourHandTicksInANano = 1L
private const val nanosecondsToSecondsMultiplier = 1.0e9.toLong()

fun main() {
    val totalCases = Integer.parseInt(nextLine())
    for (caseNumber in 1..totalCases) {
        val hands = nextLine().split(" ").map { it.toLong() }
        var finalTime = Time()

        val possibleTimes = findPossibleHourMinuteHands(hands).map {
            Time(it.first, it.second, hands.minus(it.first).minus(it.second)[0])
        }.filter { it.checkSecondsAreValid() }
        check(possibleTimes.size == 1) { "List too long" }
        if (possibleTimes.isNotEmpty()) {
            finalTime = possibleTimes[0]
        }

        println("Case #$caseNumber: " + finalTime.getPrintout())
    }
}

/**
 * @return pairs where the time is valid if first is type [HandType.HOURS] and second is type [HandType.MINUTES]
 */
private fun findPossibleHourMinuteHands(input: List<Long>): List<Pair<Long, Long>> {
    val returnList = mutableListOf<Pair<Long, Long>>()
    for (i in input.indices) {
        for (j in input.indices) {
            val pair = input[i] to input[j]
            if (i == j || returnList.contains(pair)) {
                continue
            }
            val iAsDecimalHours = input[i].toDouble() / HandType.HOURS.tickDivider
            if (
                    floor(iAsDecimalHours * HandType.HOURS.multiplierToLower) // i as a whole number of minutes
                    == floor(iAsDecimalHours) * HandType.HOURS.multiplierToLower // i as a whole number of hours (represented in minutes)
                    + floorConvert(input[j], HandType.MINUTES) // j minutes
            ) {
                returnList.add(pair)
            }
        }
    }
    return returnList
}

/**
 * Identifies the types of each hand and can store the nanoseconds too
 */
private class Time(var hoursTick: Long = 0, var minutesTick: Long = 0, var secondsTick: Long = 0, var nanoTick: Long = 0) {
    fun getPrintout(): String {
        val items = listOf(
                floorConvert(hoursTick, HandType.HOURS),
                floorConvert(minutesTick, HandType.MINUTES),
                floorConvert(secondsTick, HandType.SECONDS),
                floorConvert(nanoTick, HandType.NANOSECONDS)
        )
        return items.joinToString(" ")
    }

    fun checkSecondsAreValid(): Boolean {
        val hoursDecimal = hoursTick.toDouble() / HandType.HOURS.tickDivider
        val minutesDecimal = minutesTick.toDouble() / HandType.MINUTES.tickDivider

        var remainder = hoursDecimal - floor(hoursDecimal) // remove hours
        remainder = remainder * HandType.HOURS.multiplierToLower - floor(minutesDecimal) // remove minutes
        return (remainder * HandType.MINUTES.multiplierToLower).roundToLong() * HandType.SECONDS.tickDivider == secondsTick
    }
}

private fun floorConvert(nano: Long, divider: HandType): Long {
    if (nano == 0L) return 0L
    return floor(nano.toDouble() / divider.tickDivider).toLong()
}

private enum class HandType(val order: Int, val tickDivider: Long, val multiplierToLower: Long) {
    HOURS(3, hourHandTicksInANano * (nanosecondsToSecondsMultiplier * 60 * 60), 60),
    MINUTES(2, minuteHandTicksInANano * (nanosecondsToSecondsMultiplier * 60), 60),
    SECONDS(1, secondHandTicksInANano * nanosecondsToSecondsMultiplier, 1.0e9.toLong()),
    NANOSECONDS(0, 60 * 1.0e9.toLong(), 0);

    fun getHigher(): HandType {
        return when (this) {
            HOURS -> throw IllegalStateException("Has no higher")
            MINUTES -> HOURS
            SECONDS -> MINUTES
            NANOSECONDS -> SECONDS
        }
    }

    fun getLower(): HandType {
        return when (this) {
            HOURS -> MINUTES
            MINUTES -> SECONDS
            SECONDS -> NANOSECONDS
            NANOSECONDS -> throw IllegalStateException("Has no lower")
        }
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
