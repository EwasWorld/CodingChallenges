package googleCodeJam.mar2022.qualifying

class TimeAccumulator {
    private val startTime = System.currentTimeMillis()
    private var lastTime = startTime

    private val logs = mutableMapOf<Int, Long>()

    fun logTime(id: Int, printNow: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        val totalTimeTaken = currentTime - startTime
        val lapTimeTaken = currentTime - lastTime
        lastTime = currentTime

        val log = "$id - lap: $lapTimeTaken ms - total: $totalTimeTaken ms"
        if (printNow) {
            println(log)
        }
        else {
            val currentLoggedTime = logs[id] ?: 0
            logs[id] = currentLoggedTime + lapTimeTaken
        }
    }

    fun printFinal(printThreshold: Int? = null, printSorted: Boolean = true) {
        val totalTimeTaken = lastTime - startTime
        if (printThreshold != null && totalTimeTaken > printThreshold) {
            return
        }
        logs.forEach {
            println("${it.key} ${it.value}")
        }
        if (printSorted) {
            println("Total: $totalTimeTaken")
        }
    }
}