package example.spring.boot.resilience.retry

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class DownstreamServiceWithAnnotationBasedRetry(
    private val tripwire: Tripwire
) {

    private val log = getLogger(javaClass)

    @Retry(name = "downstream-service")
    fun getNumberOfPages(isbn: String): Int? = doGetNumberOfPages(isbn)

    @Retry(name = "downstream-service", fallbackMethod = "getNumberOfPagesFallback")
    fun getNumberOfPagesWithFallback(isbn: String): Int? = doGetNumberOfPages(isbn)

    private fun doGetNumberOfPages(isbn: String): Int {
        // imagine an HTTP call here
        tripwire.possiblyThrowException()
        return 42
    }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}
