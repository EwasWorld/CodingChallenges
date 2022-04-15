package googleCodeJam.mar2022.qualifying.problem5

import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates

fun main() {
//    val judge: Problem5.JudgeInterface = Problem5.ActualJudgeInterface()
    val judge: Problem5.JudgeInterface = Problem5.MockJudgeInterface()
    val totalTestCases = judge.getTestCaseCount()
    for (testCaseIndex in 1..totalTestCases) {
        val initialInfo = judge.startTestCase()

        val checksToPerform = initialInfo.numberOfRooms.coerceAtMost(initialInfo.numberOfAllowedOperations)
        var totalPassagesSeen = initialInfo.currentRoom.numberOfPassages.toLong()
        for (roomIndex in 0 until checksToPerform) {
            if (roomIndex == initialInfo.currentRoom.index) {
                continue
            }

            val newRoom = judge.teleport(roomIndex)
            totalPassagesSeen += newRoom.numberOfPassages
        }

        val estimate = (totalPassagesSeen / 2.0) * (checksToPerform.toDouble() / initialInfo.numberOfRooms)
        judge.estimate(estimate.roundToInt())
    }
}

object Problem5 {
    open class Room(
        val index: Int,
        numberOfPassages: Int = 0
    ) {
        var numberOfPassages = numberOfPassages
            protected set
    }

    interface JudgeInterface {
        fun getTestCaseCount(): Int
        fun startTestCase(): StartTestCaseResponse
        fun walk(): Room
        fun teleport(roomIndex: Int): Room
        fun estimate(numberOfPassages: Int)

        data class StartTestCaseResponse(
            val numberOfRooms: Int,
            val numberOfAllowedOperations: Int,
            val currentRoom: Room
        )
    }

    class ActualJudgeInterface : JudgeInterface {
        val input = Scanner(System.`in`)

        private fun String.parseRoom(): Room {
            val parsedIntegers = split(" ").map { it.toInt() }
            check(parsedIntegers[0] != -1) { "The judge has deemed you unworthy" }
            return Room(parsedIntegers[0] - 1, parsedIntegers[1])
        }

        override fun getTestCaseCount(): Int {
            return input.nextLine().toInt()
        }

        override fun startTestCase(): JudgeInterface.StartTestCaseResponse {
            val caseDetails = input.nextLine().split(" ").map { it.toInt() }
            return JudgeInterface.StartTestCaseResponse(
                    caseDetails[0],
                    caseDetails[1],
                    input.nextLine().parseRoom()
            )
        }

        override fun walk(): Room {
            println("W")
            return input.nextLine().parseRoom()
        }

        override fun teleport(roomIndex: Int): Room {
            println("T ${roomIndex + 1}")
            return input.nextLine().parseRoom()
        }

        override fun estimate(numberOfPassages: Int) {
            println("E $numberOfPassages")
        }
    }

    class MockJudgeInterface(connections: String? = null) : JudgeInterface {
        private val rooms: List<DetailedRoom>
        private var currentRoom: DetailedRoom
        private var passageCount by Delegates.notNull<Int>()

        init {
            val connectionsActual = connections ?: """
                2 3 5
                1
                1
                5
                4 1
            """.trimIndent()
            val roomStrings = connectionsActual.split("\n")
            rooms = List(roomStrings.size) { DetailedRoom(it) }
            roomStrings.forEachIndexed { index, roomPassagesString ->
                rooms[index].leadsTo = roomPassagesString.split(" ").map { rooms[it.toInt() - 1] }
            }
            var passageCountTally = 0
            rooms.forEachIndexed { index, room ->
                room.leadsTo.forEach { checkingRoom ->
                    check(checkingRoom.leadsTo.any { it.index == index })
                    { "Room $index is not bidirectional with ${checkingRoom.index}" }
                }
                passageCountTally += room.leadsTo.size
            }
            passageCount = passageCountTally / 2
            currentRoom = rooms.random()
        }

        override fun getTestCaseCount(): Int {
            return 1
        }

        override fun startTestCase(): JudgeInterface.StartTestCaseResponse {
            return JudgeInterface.StartTestCaseResponse(
                    rooms.size,
                    Int.MAX_VALUE,
                    currentRoom
            )
        }

        override fun walk(): Room {
            currentRoom = currentRoom.leadsTo.random()
            return currentRoom
        }

        override fun teleport(roomIndex: Int): Room {
            currentRoom = rooms[roomIndex]
            return currentRoom
        }

        override fun estimate(numberOfPassages: Int) {
            val correctTolerance = passageCount / 3
            val correctRange = (passageCount - correctTolerance)..(passageCount + correctTolerance)
            val estimateIsInRange = numberOfPassages in correctRange
            val correctnessString = (if (estimateIsInRange) "" else "in") + "correct"
            println("Guess was: $correctnessString (Actual: $passageCount, Estimate: $numberOfPassages)")
        }

        class DetailedRoom(
            index: Int
        ) : Room(index) {
            var leadsTo: List<DetailedRoom> = listOf()
                set(value) {
                    field = value
                    numberOfPassages = value.size
                }
        }
    }
}