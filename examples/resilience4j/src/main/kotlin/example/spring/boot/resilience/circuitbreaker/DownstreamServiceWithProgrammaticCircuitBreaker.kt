package example.spring.boot.resilience.circuitbreaker

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.decorators.Decorators
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class DownstreamServiceWithProgrammaticCircuitBreaker(
    private val circuitBreaker: CircuitBreaker,
    private val tripwire: Tripwire,
) {

    private val log = getLogger(javaClass)

    fun getNumberOfPages(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withCircuitBreaker(circuitBreaker)
            .get()

    fun getNumberOfPagesWithFallback(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withCircuitBreaker(circuitBreaker)
            .withFallback { ex -> getNumberOfPagesFallback(isbn, ex) }
            .get()

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

@Configuration
class DownstreamServiceWithProgrammaticCircuitBreakerConfiguration {

    @Bean
    fun downstreamServiceWithProgrammaticCircuitBreaker(registry: CircuitBreakerRegistry, tripwire: Tripwire) =
        DownstreamServiceWithProgrammaticCircuitBreaker(
            circuitBreaker = registry.circuitBreaker("downstream-service"),
            tripwire = tripwire
        )

}
