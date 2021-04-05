package googleCodeJam.mar2021.qualifying.problem2

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

/**
 * The elements that can create an art piece
 */
enum class ArtPieceElement {
    C, J, UNKNOWN;

    fun getOpposite(): ArtPieceElement {
        return when (this) {
            C -> J
            J -> C
            else -> throw IllegalStateException("no opposite defined")
        }
    }

    override fun toString(): String {
        if (this == UNKNOWN) return "?"
        return super.toString()
    }
}

val input = Scanner(BufferedReader(InputStreamReader(System.`in`)))
//val input = Scanner(File("src\\googleCodeJam\\mar2021\\qualifying\\problem2\\customInput.txt"))

fun main() {
    val totalCases = Integer.parseInt(input.nextLine())
    for (caseNumber in 1..totalCases) {
        /*
         * Parse input
         */
        // Allow for some breaking lines in the input for readability of my test files
        var line: String
        while (true) {
            line = input.nextLine()
            if (line.startsWith("---")) {
                println("---------")
                continue
            }
            break
        }

        val splitLine = line.split(" ")
        val cjCost = Integer.parseInt(splitLine[0])
        val jcCost = Integer.parseInt(splitLine[1])
        var artPiece = mutableListOf<ArtPieceElement>()
        for (element in splitLine[2]) {
            try {
                artPiece.add(ArtPieceElement.valueOf(element.toUpperCase().toString()))
            }
            catch (e: IllegalArgumentException) {
                artPiece.add(ArtPieceElement.UNKNOWN)
            }
        }

        /*
         * Fill unknowns at the start and end
         */
        if (artPiece[0] == ArtPieceElement.UNKNOWN) {
            artPiece = fillUnknownsAtStart(artPiece, cjCost, jcCost)
        }
        if (artPiece.last() == ArtPieceElement.UNKNOWN) {
            // Pretend the end is the start :P
            artPiece = fillUnknownsAtStart(artPiece.asReversed(), jcCost, cjCost).asReversed()
        }

        /*
         * Fill unknowns that fall between two known elements
         */
        while (artPiece.contains(ArtPieceElement.UNKNOWN)) {
            var firstUnknownIndex = artPiece.indexOfFirst { it == ArtPieceElement.UNKNOWN }
            val lastUnknownIndex = artPiece.withIndex().indexOfFirst {
                it.index > firstUnknownIndex && it.value != ArtPieceElement.UNKNOWN
            } - 1
            var elementBeforeUnknowns = artPiece[firstUnknownIndex - 1]
            val elementAfterUnknowns = artPiece[lastUnknownIndex + 1]

            // Force elementBeforeUnknowns == elementAfterUnknowns by changing the first unknown to elementAfterUnknowns
            // because there's no way to avoid making at least one pair
            if (elementBeforeUnknowns != elementAfterUnknowns) {
                artPiece[firstUnknownIndex] = elementAfterUnknowns
                elementBeforeUnknowns = elementAfterUnknowns
                firstUnknownIndex++
            }
            if (firstUnknownIndex <= lastUnknownIndex) {
                artPiece = replaceSublist(
                        artPiece,
                        generateElements(
                                elementBeforeUnknowns,
                                lastUnknownIndex - firstUnknownIndex + 1,
                                cjCost + jcCost < 0
                        ).asReversed(),
                        firstUnknownIndex
                )
            }
        }

        println("Case #$caseNumber: " + calculateArtCost(artPiece, cjCost, jcCost))
    }
}

/**
 * @return [artPiece] with [filling] overwriting [filling].size elements starting at [startIndex]
 * @throws IllegalStateException if [artPiece] is not large enough to be overwritten by [filling]
 */
fun replaceSublist(
    artPiece: List<ArtPieceElement>, filling: List<ArtPieceElement>, startIndex: Int
): MutableList<ArtPieceElement> {
    check(artPiece.size > startIndex + filling.size) { "Not enough elements in artPiece" }
    if (filling.isEmpty()) return artPiece.toMutableList()

    return artPiece.subList(0, startIndex)
            .plus(filling)
            .plus(artPiece.subList(startIndex + filling.size, artPiece.size))
            .toMutableList()
}

/**
 * @param size the size of the list to generate
 * @return minimum cost list if all elements are unknowns
 */
fun fillListOfUnknowns(size: Int, cjCost: Int, jcCost: Int): MutableList<ArtPieceElement> {
    // Both positive or trivially sized list: return a 0-cost list
    if (cjCost >= 0 && jcCost >= 0 || size == 1) {
        return generateElements(ArtPieceElement.J, size, false)
    }
    // At least one negative but cost of alternating would result in a loss: just do a single negative-cost pair
    if (cjCost + jcCost > 0 && size > 2) {
        val startElement = if (cjCost < 0) ArtPieceElement.C else ArtPieceElement.J
        val artPiece = generateElements(startElement.getOpposite(), size, false)
        artPiece[0] = startElement
        return artPiece
    }
    // Alternate starting on the negative(est)-cost pair to ensure it maximises that combination
    val startingElement = if (cjCost < jcCost) ArtPieceElement.C else ArtPieceElement.J
    val artPiece = generateElements(startingElement.getOpposite(), size, true)
    // Ensure that the final pair is not positive (no need to check start as we guarantee to start on a negative pair)
    if (artPiece[artPiece.size - 1] == startingElement && (cjCost > 0 || jcCost > 0)) {
        artPiece[artPiece.size - 1] = startingElement.getOpposite()
    }
    return artPiece
}

/**
 * @return a list of [size] elements, it assumes it's generating items to come AFTER [firstKnownElement]
 * @param alternateElements true: alternates between J and C starting with the opposite of [firstKnownElement].
 * false: generates a list of [firstKnownElement]
 */
fun generateElements(
    firstKnownElement: ArtPieceElement, size: Int, alternateElements: Boolean
): MutableList<ArtPieceElement> {
    return MutableList(size) { index ->
        if (alternateElements && index % 2 == 0) firstKnownElement.getOpposite() else firstKnownElement
    }
}

/**
 * Note: Will ignore [ArtPieceElement.UNKNOWN]s
 *
 * @return the cost of [artPiece] given a single CJ string costs [cjCost] and a single JC string costs [jcCost]
 */
fun calculateArtCost(artPiece: List<ArtPieceElement>, cjCost: Int, jcCost: Int): Int {
    val stringArt = artPiece.joinToString("")
    val cjTotalCost = (stringArt.length - stringArt.replace("CJ", "").length) * cjCost / 2
    val jcTotalCost = (stringArt.length - stringArt.replace("JC", "").length) * jcCost / 2
    return cjTotalCost + jcTotalCost
}

/**
 * If [artPiece] starts with N ArtElement.X elements, it will optimise the first N elements and return an amended art piece
 * @return [artPiece] after replacing all [ArtPieceElement.UNKNOWN]s until the first non-unknown item
 */
fun fillUnknownsAtStart(
    artPiece: MutableList<ArtPieceElement>, cjCost: Int, jcCost: Int
): MutableList<ArtPieceElement> {
    var unknownsToFill = artPiece.indexOfFirst { it != ArtPieceElement.UNKNOWN }
    if (unknownsToFill == -1) {
        return fillListOfUnknowns(artPiece.size, cjCost, jcCost)
    }
    if (unknownsToFill == 0) {
        return artPiece
    }

    /*
     * Special case: if CJ XOR JC has a negative cost and CJ + JC > 0, make a single negative pair if possible
     * then make the rest identical
     */
    val firstKnownElement = artPiece[unknownsToFill]
    if (cjCost + jcCost > 0 && (cjCost < 0 || jcCost < 0)) {
        if (cjCost < 0 && firstKnownElement == ArtPieceElement.J) {
            artPiece[unknownsToFill - 1] = ArtPieceElement.C
            unknownsToFill--
        }
        else if (jcCost < 0 && firstKnownElement == ArtPieceElement.C) {
            artPiece[unknownsToFill - 1] = ArtPieceElement.J
            unknownsToFill--
        }
        return replaceSublist(artPiece, generateElements(artPiece[unknownsToFill], unknownsToFill, false), 0)
    }

    /*
     * General case: Fill with alternating elements then remove new pairs until a negative-cost pair is hit
     * If costs are: both positive/zero - remove all, both negative - keep all
     *               only one negative - maximise number of pairs, ensure last pair is negative cost
     *                 (given case where the sum of costs is >0 has already been handled)
     */
    val finalArt = replaceSublist(artPiece, generateElements(firstKnownElement, unknownsToFill, true).asReversed(), 0)
    var identicalUntil = 0
    while (identicalUntil < unknownsToFill
            && finalArt[identicalUntil] != finalArt[identicalUntil + 1]
            && ((finalArt[identicalUntil] == ArtPieceElement.C && cjCost >= 0)
                    || (finalArt[identicalUntil] == ArtPieceElement.J && jcCost >= 0))
    ) {
        identicalUntil++
    }
    return replaceSublist(finalArt, generateElements(finalArt[identicalUntil], identicalUntil, false), 0)
}
