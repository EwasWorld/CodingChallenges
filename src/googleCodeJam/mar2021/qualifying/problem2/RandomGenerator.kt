package googleCodeJam.mar2021.qualifying.problem2

import java.io.File
import kotlin.random.Random

fun main() {
    val output = File("src\\googleCodeJam\\mar2021\\qualifying\\problem2\\generatedInput.txt")
    val generatedArtCount = 50
    output.writeText((generatedArtCount * 8).toString() + "\n")
    for (x in 0 until generatedArtCount) {
        val art = mutableListOf<ArtElement>()
        val startWithAnX = Random.nextBoolean()
        for (i in 0 until Random.nextInt(6, 12)) {
            val count = Random.nextInt(6) + 1
            if (i % 2 == 0 == startWithAnX) {
                art.addAll(MutableList(count) { ArtElement.X })
            }
            else {
                for (j in 0 until count) {
                    art.add(if (Random.nextBoolean()) ArtElement.C else ArtElement.J)
                }
            }
        }
        val artString = art.joinToString("") { it.toString() }
        output.appendText("0 0 $artString\n")
        output.appendText("5 0 $artString\n")
        output.appendText("0 5 $artString\n")
        output.appendText("5 5 $artString\n")
        output.appendText("4 1 $artString\n")
        output.appendText("4 -1 $artString\n")
        output.appendText("-4 -1 $artString\n")
        output.appendText("-4 1 $artString\n")
    }
}