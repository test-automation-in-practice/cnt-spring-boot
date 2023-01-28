package example.spring.boot.resilience.circuitbreaker

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class DownstreamServiceWithAnnotationBasedCircuitBreaker(
    private val tripwire: Tripwire
) {

    private val log = getLogger(javaClass)

    @CircuitBreaker(name = "downstream-service")
    fun getNumberOfPages(isbn: String): Int? = doGetNumberOfPages(isbn)

    @CircuitBreaker(name = "downstream-service", fallbackMethod = "getNumberOfPagesFallback")
    fun getNumberOfPagesWithFallback(isbn: String): Int? = doGetNumberOfPages(isbn)

    private fun doGetNumberOfPages(isbn: String): Int? {
        // imagine an HTTP call here
        tripwire.possiblyThrowException()
        return 42
    }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}
