package caching.calculation

import org.springframework.stereotype.Service

@Service
class ManualCacheFibonacciCalculator {

    private val cache = mutableMapOf<Int, Long>()

    fun fibonacci(number: Int): Long {
        val cachedResult = cache[number]
        if (cachedResult != null) {
            return cachedResult
        }

        val result = if (number > 2) {
            fibonacci(number - 1) + fibonacci(number - 2)
        } else {
            1
        }
        cache[number] = result
        return result
    }

}