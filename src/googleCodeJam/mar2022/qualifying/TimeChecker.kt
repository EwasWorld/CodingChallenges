package googleCodeJam.mar2022.qualifying

class TimeChecker {
    private val startTime = System.currentTimeMillis()
    private var lastTime = startTime

    private val logs = mutableListOf<Pair<String, Long>>()

    fun logTime(id: String = "", printNow: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        val totalTimeTaken = currentTime - startTime
        val lapTimeTaken = currentTime - lastTime
        lastTime = currentTime

        val log = "$id - lap: $lapTimeTaken ms - total: $totalTimeTaken ms"
        if (printNow) {
            println(log)
        }
        else {
            logs.add(log to lapTimeTaken)
        }
    }

    fun printFinal(printThreshold: Int? = null, printSorted: Boolean = true) {
        val totalTimeTaken = lastTime - startTime
        if (printThreshold != null && totalTimeTaken > printThreshold) {
            return
        }
        if (printSorted) {
            logs.sortByDescending { it.second }
        }
        logs.forEach {
            println(it.first)
        }
        if (printSorted) {
            println("Total: $totalTimeTaken")
        }
    }
}