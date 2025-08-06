package example.spring.boot.resilience.ratelimiter

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class DownstreamServiceWithAnnotationBasedRateLimiter {

    private val log = getLogger(javaClass)

    @RateLimiter(name = "downstream-service")
    fun getNumberOfPages(isbn: String): Int? = doGetNumberOfPages(isbn)

    @RateLimiter(name = "downstream-service", fallbackMethod = "getNumberOfPagesFallback")
    fun getNumberOfPagesWithFallback(isbn: String): Int? = doGetNumberOfPages(isbn)

    private fun doGetNumberOfPages(isbn: String): Int {
        // imagine an HTTP call here
        return 42
    }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}
