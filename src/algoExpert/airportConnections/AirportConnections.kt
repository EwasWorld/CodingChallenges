package algoExpert.airportConnections

fun main() {
    val airports = listOf(
            "BGI", "CDG", "DEL", "DOH", "DSM",
            "EWR", "EYW", "HND", "ICN", "JFK",
            "LGA", "LHR", "ORD", "SAN", "SFO",
            "SIN", "TLV", "BUD"
    )
    val connections = listOf(
            "BGI" to "LGA", "CDG" to "SIN", "DEL" to "DOH",
            "CDG" to "BUD", "DEL" to "CDG", "DSM" to "ORD",
            "EWR" to "HND", "EYW" to "LHR", "HND" to "ICN",
            "HND" to "JFK", "ICN" to "JFK", "JFK" to "LGA",
            "LHR" to "SFO", "ORD" to "BGI", "SAN" to "EYW",
            "SFO" to "SAN", "SFO" to "DSM", "SIN" to "CDG",
            "TLV" to "DEL"
    )
    println(Airports.execute("LGA", airports, connections))
}

object Airports {
    /**
     * All airports are represented by a unique three-letter code (e.g. "LHR")
     *
     * @param startingAirport the airport which needs to connect to all other airports
     * @param airportsInput a list of all possible airports
     * @param connectionsInput a list of pairs where `A to B` represents a one-way connection from airport A to airport B
     * @return the minimum number of connections that must be added to [connectionsInput] so that
     * any airport in [airportsInput] can be reached from [startingAirport]
     */
    fun execute(
        startingAirport: String,
        airportsInput: Iterable<String>,
        connectionsInput: Iterable<Pair<String, String>>
    ): Int {
        /*
         * Parse inputs
         */
        val airports = airportsInput.associateWith { listOf<String>() }
                .plus(connectionsInput.groupBy { it.first }.mapValues { it.value.map { pair -> pair.second } })
                .map { Airport(it.key, it.value.toSet()) }
        val airportsMap = airports.associateBy { it.name }
        airports.forEach { it.completeAirport(airportsMap) }

        /*
         * Find airports that can't be reached from anywhere
         */
        val unreachableAirports = airports.toSet().minus(airports.map { it.connections }.flatten())

        /*
         * Find all loops of airports and represent them with a single airport
         */
        val airportsInALoop = mutableSetOf<Airport>()
        val airportLoopCheckMap = airportsMap.mapValues { it.value to false }.toMutableMap()
        for (airport in airports) {
            if (checkForConnection(
                            airport, airport, airportLoopCheckMap
                    )
            ) {
                airportsInALoop.removeAll(airport.connections.intersect(airportsInALoop))
                airportsInALoop.add(airport)
            }
        }

        val unreachableConnections = unreachableAirports.map { it.connections }.flatten().toSet()
        return unreachableAirports.plus(airportsInALoop).filterNot {
            airportsMap[startingAirport]!!.connections.contains(it) || unreachableConnections.contains(it)
        }.size
    }

    /**
     * @return whether [currentAirport] has a connection to [searchFor]
     */
    private fun checkForConnection(
        currentAirport: Airport,
        searchFor: Airport,
        seenAirports: MutableMap<String, Pair<Airport, Boolean>>
    ): Boolean {
        val current = seenAirports[currentAirport.name]!!
        if (current.second) return false
        seenAirports[currentAirport.name] = Pair(current.first, true)
        var hasLoop = false
        for (connection in currentAirport.connections) {
            val connectionPair = seenAirports[connection.name]!!.first
            if (connectionPair == searchFor) {
                hasLoop = true
            }
            checkForConnection(connectionPair, searchFor, seenAirports)
        }
        return hasLoop
    }

    class Airport(
        val name: String,
        private val stringConnections: Set<String>
    ) {
        private var complete = stringConnections.isEmpty()
        lateinit var connections: List<Airport>

        fun completeAirport(airports: Map<String, Airport>) {
            if (complete) {
                connections = stringConnections.map { airports[it]!! }
                return
            }
            complete = true
            val newConnections = stringConnections.toMutableSet()
            for (connection in stringConnections) {
                val connectionAirport = airports[connection] ?: continue
                if (!connectionAirport.complete) {
                    connectionAirport.completeAirport(airports)
                }
                newConnections.addAll(connectionAirport.stringConnections)
            }
            connections = stringConnections.map { airports[it]!! }
        }

        override fun toString(): String {
            return name
        }
    }
}