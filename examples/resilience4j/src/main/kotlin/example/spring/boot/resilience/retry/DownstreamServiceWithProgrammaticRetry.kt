package example.spring.boot.resilience.retry

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryRegistry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class DownstreamServiceWithProgrammaticRetry(
    private val retry: Retry,
    private val tripwire: Tripwire
) {

    private val log = getLogger(javaClass)

    fun getNumberOfPages(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withRetry(retry)
            .get()

    fun getNumberOfPagesWithFallback(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withRetry(retry)
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
class DownstreamServiceWithProgrammaticRetryConfiguration {

    @Bean
    fun downstreamServiceWithProgrammaticRetry(registry: RetryRegistry, tripwire: Tripwire) =
        DownstreamServiceWithProgrammaticRetry(
            retry = registry.retry("downstream-service"),
            tripwire = tripwire
        )

}
