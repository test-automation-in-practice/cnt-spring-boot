package caching.calculation

import org.springframework.stereotype.Service

@Service
class NoCacheFibonacciCalculator {

    fun fibonacci(number: Int): Long {
        if (number > 2) {
            return fibonacci(number - 1) + fibonacci(number - 2)
        }
        return 1
    }

}