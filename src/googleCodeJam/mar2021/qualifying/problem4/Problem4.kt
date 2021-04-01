package googleCodeJam.mar2021.qualifying.problem4

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

// Time to complete: ~30 mins for the algorithm, ~2 hrs to write the code and pass
fun main() {
//    val judge = CustomInteraction(10, 50, 300)
    val judge = JudgeInteraction()
    judge.getInitialNumbers()
    while (judge.currentTestCaseNumber < judge.testCases) {
        // Generate a starting point
        val mainList = when (judge.comparison(1, 2, 3)) {
            1 -> mutableListOf(2, 1, 3)
            2 -> mutableListOf(1, 2, 3)
            3 -> mutableListOf(1, 3, 2)
            else -> throw IllegalStateException("Comparison failed")
        }

        for (i in 4..judge.listSize) {
            var listStartIndex = 0
            var currentList: List<Int> = mainList
            while (true) {
                var separatedLists = mutableListOf<List<Int>>()
                var comparisonItems = mutableListOf<Int>()
                // 2 will be compared with the new unknown
                var totalFree = currentList.size - 2
                if (totalFree == -1) {
                    separatedLists = mutableListOf(listOf(), listOf(), listOf())
                    if (listStartIndex + 1 >= mainList.size) {
                        listStartIndex -= 1
                    }
                    comparisonItems = mainList.subList(listStartIndex, listStartIndex + 2)
                }
                else {
                    var currentIndex = 0
                    for (j in 0..1) {
                        val desiredSublist = desiredSublistSize(totalFree)
                        separatedLists.add(currentList.subList(currentIndex, currentIndex + desiredSublist))
                        currentIndex += desiredSublist
                        totalFree -= desiredSublist
                        comparisonItems.add(currentList[currentIndex])
                        currentIndex++
                    }
                    separatedLists.add(currentList.subList(currentIndex, currentList.size))
                }
                when (judge.comparison(comparisonItems[0], comparisonItems[1], i)) {
                    comparisonItems[0] -> {
                        currentList = separatedLists[0]
                    }
                    i -> {
                        listStartIndex += separatedLists[0].size + 1
                        currentList = separatedLists[1]
                    }
                    comparisonItems[1] -> {
                        listStartIndex += separatedLists[0].size + 1 + separatedLists[1].size + 1
                        currentList = separatedLists[2]
                    }
                    else -> throw IllegalStateException("Comparison failed")
                }
                if (currentList.isEmpty()) {
                    break
                }
            }
            mainList.add(listStartIndex, i)
        }
        if (!judge.guess(mainList)) {
            throw IllegalStateException("Incorrect guess, terminating")
        }
    }
}

fun desiredSublistSize(items: Int): Int {
    var sublistSize = 2
    var nextSublistSize: Int
    while (true) {
        if (items <= sublistSize) {
            return items
        }
        nextSublistSize = sublistSize * 3 + 2
        if (nextSublistSize > items) {
            return sublistSize
        }
        sublistSize = nextSublistSize

        if (sublistSize > 1000) {
            throw IllegalStateException("Shouldn't get this high")
        }
    }
}

open class JudgeInteraction {
    val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
    var maxGuesses = 0
    var listSize = 0
    var testCases = 0
    var currentGuessCount = 0
    var currentTestCaseNumber = 0

    open fun getInitialNumbers() {
        val inputValues = input.nextLine().split(" ").map { Integer.parseInt(it) }
        testCases = inputValues[0]
        listSize = inputValues[1]
        maxGuesses = inputValues[2]
    }

    /**
     * Writes the query to std out and returns the response
     *
     * @params the indexes of the three items to be compared
     * @return the index of the median item (the item that is neither the min nor the max)
     */
    open fun comparison(a: Int, b: Int, c: Int): Int {
        println("$a $b $c")
        val answer = Integer.parseInt(input.nextLine())
        if (answer < 0) {
            throw IllegalStateException("Too many guesses")
        }
        return answer
    }

    /**
     * Writes the query to std out and returns the response
     *
     * @param list the indexes of the ordered items (can be ordered low to high or high to low)
     * @return true if the guess was correct
     */
    open fun guess(list: List<Int>): Boolean {
        currentTestCaseNumber++
        println(list.joinToString(" "))
        return Integer.parseInt(input.nextLine()) == 1
    }

    fun checkGuessCount() {
        if (currentGuessCount++ >= maxGuesses) {
            throw IllegalStateException("Too many guesses")
        }
    }
}

/**
 * Simulates the judge system to interact with but generates its own list and responds based on that
 */
class CustomInteraction(testCases: Int, listSize: Int, maxGuesses: Int): JudgeInteraction() {
    private var list = listOf<Int>()

    init {
        this.maxGuesses = maxGuesses
        this.listSize = listSize
        this.testCases = testCases
        resetList()
    }

    private fun resetList() {
        currentGuessCount = 0
        list = List(listSize) { index -> index + 1 }.shuffled()
        println(list)
//        list = listOf(6, 3, 7, 4, 8, 5, 9, 10, 2, 1)
    }

    override fun getInitialNumbers() {
        // Do nothing, initial numbers are provided on creation of the class
    }

    override fun comparison(a: Int, b: Int, c: Int): Int {
        checkGuessCount()
        return mutableListOf(a to list[a - 1], b to list[b - 1], c to list[c - 1]).sortedBy { it.second }[1].first
    }

    override fun guess(list: List<Int>): Boolean {
        val correctIndexedList = this.list.mapIndexed { index, i -> i to index + 1 }.sortedBy { it.first }
        val correctList = correctIndexedList.map { it.second }
        val isCorrect = list == correctList || list == correctList.reversed()
        currentTestCaseNumber++
        println(currentGuessCount)
        resetList()
        return isCorrect
    }
}

/*
Adding one more to a 3-element list:
a b c
compare(x a b)
- Return a: new list: x a b c
- Return x: new list: a x b c
- Return b: compare(x b c)
    - Return x: new list: a b x c
    - Return b: new list: impossible
    - Return c: new list: a b c x

a b c d
compare(b c x)
- Return b: compare(a b x)
- Return c: compare(c d x)
- Return x: new list: a b x c d

a b c d e
compare(b d x)
- Return b: compare(a b x)
- Return d: compare(d e x)
- Return x: compare(b c x)

a b c d e f
compare(b d x)
- Return b: compare(a b x)
- Return d: compare(e f x)
- Return x: compare(b c x)

a b c d e f g h i
compare(c f x)
- Return f: see abc


Goal:
1. break down into sublists of 2 (which can be done in 2 comparisons)
    - make sublists of 0 wherever possible, e.g. if there are 6 items therefore 2 being compared and 4 free to group, go for sublists of 2,0,2
2. otherwise break down into sublists of 8

Each optimal sublist size is calculated by x*3+2 where x is the previous sublist size (start at 2)
Sublists of 2 will take worst case 2 comparisons, each level adds 1
2, 8, 26

Starting with 3 items, comparing once
5 items added with worst case 2 comparisons (brings total items to 8) = 11
18 items added with worst case 3 comparisons (brings total to 26 items) = 78 + 11 = 89
24 items added with worst case 4 comparisons (brings total to 50 items) = 96 + 89 = 185
 */