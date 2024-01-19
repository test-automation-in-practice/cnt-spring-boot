package example.spring.boot.resilience.bulkhead

import io.github.resilience4j.bulkhead.annotation.Bulkhead
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class DownstreamServiceWithAnnotationBasedBulkhead {

    private val log = getLogger(javaClass)

    @Bulkhead(name = "downstream-service")
    fun getNumberOfPages(isbn: String): Int? = doGetNumberOfPages(isbn)

    @Bulkhead(name = "downstream-service", fallbackMethod = "getNumberOfPagesFallback")
    fun getNumberOfPagesWithFallback(isbn: String): Int? = doGetNumberOfPages(isbn)

    private fun doGetNumberOfPages(isbn: String): Int {
        // imagine an HTTP call here
        Thread.sleep(10)
        return 42
    }

    // Needs to be public because otherwise a CGLIB proxy is used where the `log` is null
    fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}
