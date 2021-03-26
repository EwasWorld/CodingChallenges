package googleCodeJam.mar2021.qualifying.problem2

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.abs

enum class ArtElement { C, J, X;
    fun getOpposite(): ArtElement {
        return when (this) {
            C -> J
            J -> C
            else -> throw IllegalArgumentException("no opposite defined");
        }
    }
}

//val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
val input = Scanner(File("src\\googleCodeJam\\mar2021\\qualifying\\problem2\\customInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        val line = input.nextLine().split(" ")
        val cjCost = Integer.parseInt(line[0])
        val jcCost = Integer.parseInt(line[1])
        var art = mutableListOf<ArtElement>()
        for (element in line[2]) {
            try {
                art.add(ArtElement.valueOf(element.toUpperCase().toString()))
            }
            catch (e: IllegalArgumentException) {
                art.add(ArtElement.X)
            }
        }

        /*
         * Fill start and end unknowns
         */
        if (art[0] == ArtElement.X) {
            art = fillUnknownsAtStart(art, cjCost, jcCost)
        }
        if (art.last() == ArtElement.X) {
            // Pretend the end is the start :P
            art = fillUnknownsAtStart(art.asReversed(), jcCost, cjCost).asReversed()
        }

        /*
         * Fill unknowns that fall between two known elements
         */
        var firstUnknownIndex: Int? = null
        for (i in art.indices) {
            // Find start of unknown sequence
            if (art[i] == ArtElement.X && firstUnknownIndex == null) {
                firstUnknownIndex = i
                continue
            }
            // Check if end of unknown sequence
            if (art[i] != ArtElement.X && firstUnknownIndex != null) {
                val lastUnknownIndex = i - 1
                var startElement = art[firstUnknownIndex - 1]
                val endElement = art[lastUnknownIndex + 1]

                // Convert to a sequence that starts and ends on the same element by filling the first unknown
                //      (there's no way to avoid making at least one pair of this type anyway)
                if (startElement != endElement) {
                    art[firstUnknownIndex] = endElement
                    startElement = endElement
                    firstUnknownIndex++
                }
                if (firstUnknownIndex <= lastUnknownIndex) {
                    val filling = generateElements(startElement, lastUnknownIndex - firstUnknownIndex + 1, cjCost + jcCost < 0).asReversed()
                    art = replaceSublist(art, filling, firstUnknownIndex)
                }
                firstUnknownIndex = null
            }
        }

        println("Case #$caseNumber: " + calculateArtCost(art, cjCost, jcCost))
    }
}

fun replaceSublist(art: MutableList<ArtElement>, filling: MutableList<ArtElement>, startIndex: Int): MutableList<ArtElement> {
    val finalArt = art.filterIndexed {
            index, _ -> !(index >= startIndex && index < startIndex + filling.size) }.toMutableList()
    finalArt.addAll(startIndex, filling)
    return finalArt
}

/**
 * @return minimum cost list if all elements are unknowns
 */
fun fillListOfUnknowns(size: Int, cjCost: Int, jcCost: Int): MutableList<ArtElement> {
    // Both positive or trivially sized list: cost 0
    if (cjCost >= 0 && jcCost >= 0 || size == 1) {
        return generateElements(ArtElement.J, size, false)
    }
    // At least one negative but cost of alternating would be high: just do a single negative-cost pair
    if (cjCost + jcCost > 0 && size > 2) {
        val startElement = if (cjCost < 0) ArtElement.C else ArtElement.J
        val art = generateElements(startElement.getOpposite(), size, false)
        art[0] = startElement
        return art
    }
    // Alternate starting on the negative(est)-cost pair to ensure it maximises that one
    return generateElements(if (cjCost < jcCost) ArtElement.C else ArtElement.J, size, true)
}

/**
 * @param firstKnownElement in an alternating list, it assumes it's generating unknown items AFTER this, therefore the
 * first item will be opposite. In a non-alternating list, this item will be duplicated for the whole list
 * @param alternate true: alternates between J and C starting with the opposite of [firstKnownElement]. False: generates
 * a list of [firstKnownElement]
 */
fun generateElements(firstKnownElement: ArtElement, count: Int, alternate: Boolean): MutableList<ArtElement> {
    return MutableList(count) { index -> if (alternate && index % 2 == 0) firstKnownElement.getOpposite() else firstKnownElement }
}

fun calculateArtCost(art: List<ArtElement>, cjCost: Int, jcCost: Int): Int {
    var cost = 0
    var previousElement: ArtElement? = null
    for (element in art) {
        if (previousElement != null && previousElement != element) {
            cost += if (previousElement == ArtElement.C) cjCost else jcCost
        }
        previousElement = if (element != ArtElement.X) element else null
    }
    return cost
}

/**
 * If [art] starts with N ArtElement.X elements, it will optimise the first N elements and return an ammended art piece
 */
fun fillUnknownsAtStart(art: MutableList<ArtElement>, cjCost: Int, jcCost: Int): MutableList<ArtElement> {
    var numberOfPlacesToFill = art.indexOfFirst { it != ArtElement.X }
    if (numberOfPlacesToFill == 0) {
        return fillListOfUnknowns(art.size, cjCost, jcCost)
    }
    var firstKnownItem = art[numberOfPlacesToFill]
    val costSignIdentical = (cjCost <= 0 && jcCost <= 0) || (cjCost >= 0 && jcCost >= 0)
    val cjIsNegativeCost = cjCost < 0

    var forceNegativeEnding = false
    val alternate: Boolean
    if (costSignIdentical && !cjIsNegativeCost) {
        alternate = false
    }
    else if (numberOfPlacesToFill == 1) {
        if (costSignIdentical && cjIsNegativeCost) {
            alternate = true
        }
        else {
            alternate = false
            if (firstKnownItem == ArtElement.J && cjIsNegativeCost) {
                art[--numberOfPlacesToFill] = ArtElement.C
            }
            if (firstKnownItem == ArtElement.C && !cjIsNegativeCost) {
                art[--numberOfPlacesToFill] = ArtElement.J
            }
        }
    }
    else if (costSignIdentical && cjIsNegativeCost) {
        alternate = true
    }
    else if ((cjIsNegativeCost && abs(cjCost) >= jcCost) || (!cjIsNegativeCost && abs(jcCost) >= cjCost)) {
        alternate = true
        forceNegativeEnding = true
    }
    else {
        alternate = false
        var newItem: ArtElement? = null
        if (firstKnownItem == ArtElement.J && cjIsNegativeCost) {
            newItem = ArtElement.C
        }
        else if (firstKnownItem == ArtElement.C && !cjIsNegativeCost) {
            newItem = ArtElement.J
        }
        if (newItem != null) {
            art[--numberOfPlacesToFill] = newItem
            firstKnownItem = newItem
        }
    }

    val finalArt = replaceSublist(art, generateElements(firstKnownItem, numberOfPlacesToFill, alternate).asReversed(), 0)
    if (forceNegativeEnding) {
        if (art[0] == ArtElement.C && cjIsNegativeCost) {
            finalArt[0] = ArtElement.J
        }
        if (art[0] == ArtElement.J && !cjIsNegativeCost) {
            finalArt[0] = ArtElement.C
        }
    }
    return finalArt
}

/* Musings:
If both costs are positive
    Duplicate the last item
Else if there's just one element to assign
    If both costs are negative
        Alternate
    Else
        Make the negative-cost pair or no pair
Else if both costs are negative
    Alternate
Else if abs(negative cost) >= positive cost
    Alternate making sure to end on a negative
Else
    Make a single negative-cost pair or no pair

Actions:
No pair
Alternate
Alternate and end on best negative
Force negative or no pair
*/