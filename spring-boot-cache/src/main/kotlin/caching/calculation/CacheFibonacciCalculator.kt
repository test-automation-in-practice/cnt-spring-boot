package caching.calculation

import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class CacheFibonacciCalculator(
        @Lazy private val self: CacheFibonacciCalculator
) {

    @Cacheable("fibonacciByNumber")
    fun fibonacci(number: Int): Long {
        if (number > 2) {
            return self.fibonacci(number - 1) + self.fibonacci(number - 2)
        }
        return 1
    }

}