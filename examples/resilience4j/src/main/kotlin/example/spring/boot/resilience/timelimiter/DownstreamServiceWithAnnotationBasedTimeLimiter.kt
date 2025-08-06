package example.spring.boot.resilience.timelimiter

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletableFuture.supplyAsync

@Service
class DownstreamServiceWithAnnotationBasedTimeLimiter(
    private val tripwire: Tripwire
) {

    private val log = getLogger(javaClass)

    @TimeLimiter(name = "downstream-service")
    fun getNumberOfPages(isbn: String): CompletableFuture<Int?> = doGetNumberOfPages(isbn)

    @TimeLimiter(name = "downstream-service", fallbackMethod = "getNumberOfPagesFallback")
    fun getNumberOfPagesWithFallback(isbn: String): CompletableFuture<Int?> = doGetNumberOfPages(isbn)

    private fun doGetNumberOfPages(isbn: String): CompletableFuture<Int?> =
        supplyAsync {
            // imagine an HTTP call here
            tripwire.possiblyWait()
            42
        }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): CompletableFuture<Int?> {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return completedFuture(null)
    }

}
