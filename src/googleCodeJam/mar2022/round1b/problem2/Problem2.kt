package googleCodeJam.mar2022.round1b.problem2

import googleCodeJam.mar2022.IoHandler
import googleCodeJam.mar2022.IoHandler.Companion.checkExpectedOutput
import java.util.*

fun main() {
    val ioHandler = IoHandler(
            "src/googleCodeJam/mar2022/round1b/problem2/test_data",
            1,
            true
    )
    Problem2.execute(ioHandler)
}

object Problem2 {
    var productsPerCustomer = 0

    fun execute(ioHandler: IoHandler? = null) {
        val input = ioHandler?.input ?: Scanner(System.`in`)
        val output = ioHandler?.expectedOutput

        val testCases = input.nextLine().toInt()
        for (testCaseIndex in 1..testCases) {
            val expectedOutput = output?.nextLine()
            val (customers, productsPerCustomerIn) = input.nextLine().split(" ").let { it[0].toInt() to it[1].toInt() }
            productsPerCustomer = productsPerCustomerIn
            var previousCustomer: Customer? = null
            val forcedCustomers = mutableListOf<Int>()
            val allCustomers = List(customers) { currentCustomerIndex ->
                val customer = Customer(input.nextLine(), previousCustomer)
                previousCustomer?.nextCustomer = customer
                previousCustomer = customer
                customer
            }

            // First customer always presses from 0 to their max
            var totalPresses = allCustomers.first().max
            var currentValue = allCustomers.first().max
            for ((offIndex, customer) in allCustomers.takeLast(allCustomers.size - 1).withIndex()) {
                // First customer was ignored so actual index in `allCustomers` is `offIndex + 1`
                val index = offIndex + 1

                if (customer.nextCustomerIs == RelationshipType.LOWER
                        && customer.nextCustomerIs == customer.previousCustomerIs
                ) {
                    totalPresses += customer.minimumPressesRequired * 2
                    totalPresses += customer.min - currentValue
                    currentValue = customer.min
                    continue
                }
                if (customer.nextCustomerIs == RelationshipType.HIGHER
                        && customer.nextCustomerIs == customer.previousCustomerIs
                ) {
                    totalPresses += customer.minimumPressesRequired * 2
                    totalPresses += currentValue - customer.max
                    currentValue = customer.max
                    continue
                }

                
            }

            val outputString = "Case #$testCaseIndex: $totalPresses"
            expectedOutput?.checkExpectedOutput(outputString, true)
            println(outputString)
        }
    }

    class Customer(inputLine: String, val previousCustomer: Customer?) {
        val min: Int
        val max: Int
        val previousCustomerIs: RelationshipType?

        init {
            val products = inputLine.split(" ").map { it.toInt() }.sorted()
            check(products.size == productsPerCustomer) { "Invalid products" }
            min = products.first()
            max = products.last()
            check(max >= min) { "Invalid customer details" }

            previousCustomerIs = previousCustomer?.let { RelationshipType.getNextCustomerIs(this, previousCustomer) }
            previousCustomer?.let {
                previousCustomer.nextCustomer = this
                previousCustomer.nextCustomerIs = previousCustomerIs!!.getOpposite()
            }
        }

        var nextCustomer: Customer? = null
            set(value) {
                field = value
                nextCustomerIs = value?.let { it.previousCustomerIs?.getOpposite() }
            }
        var nextCustomerIs: RelationshipType? = null
            private set

        val minimumPressesRequired = max - min
    }

    enum class RelationshipType {
        // Ranges do not overlap
        LOWER, HIGHER,

        // One range encompasses/contains another range
        CONTAINED, ENCOMPASS,

        // Ranges partially overlap (i.e. one bound of A is contained within B, the other bound is higher/lower than B)
        OVERLAP_LOWER, OVERLAP_HIGHER,

        EQUAL;

        companion object {
            fun getNextCustomerIs(current: Customer, next: Customer) = when {
                current.max == next.max && current.min == next.min -> EQUAL
                current.min >= next.max -> LOWER
                current.max <= next.min -> HIGHER
                next.min <= current.min && next.max >= current.max -> ENCOMPASS
                next.min >= current.min && next.max <= current.max -> CONTAINED
                next.min <= current.min -> OVERLAP_LOWER
                else -> OVERLAP_HIGHER
            }
        }

        // Values are always placed next to their opposite so either go one up or one down
        fun getOpposite() = values()[(this.ordinal + if (this.ordinal % 2 == 0) 1 else -1).coerceAtMost(values().size)]
    }
}