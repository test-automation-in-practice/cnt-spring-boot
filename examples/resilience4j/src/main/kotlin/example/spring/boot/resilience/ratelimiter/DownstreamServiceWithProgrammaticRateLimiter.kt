package example.spring.boot.resilience.ratelimiter

import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class DownstreamServiceWithProgrammaticRateLimiter(
    private val rateLimiter: RateLimiter
) {

    private val log = getLogger(javaClass)

    fun getNumberOfPages(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withRateLimiter(rateLimiter)
            .get()

    fun getNumberOfPagesWithFallback(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withRateLimiter(rateLimiter)
            .withFallback { ex -> getNumberOfPagesFallback(isbn, ex) }
            .get()

    private fun doGetNumberOfPages(isbn: String): Int? {
        // imagine an HTTP call here
        return 42
    }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}

@Configuration
class DownstreamServiceWithProgrammaticRateLimiterConfiguration {

    @Bean
    fun downstreamServiceWithProgrammaticRateLimiter(registry: RateLimiterRegistry) =
        DownstreamServiceWithProgrammaticRateLimiter(
            rateLimiter = registry.rateLimiter("downstream-service")
        )

}
