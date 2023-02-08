package example.spring.boot.resilience.timelimiter

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.timelimiter.TimeLimiter
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

class DownstreamServiceWithProgrammaticTimeLimiter(
    private val scheduledExectioner: ScheduledExecutorService,
    private val timeLimiter: TimeLimiter,
    private val tripwire: Tripwire,
) {

    private val log = getLogger(javaClass)

    fun getNumberOfPages(isbn: String): CompletableFuture<Int?> =
        Decorators.ofCompletionStage { doGetNumberOfPages(isbn) }
            .withTimeLimiter(timeLimiter, scheduledExectioner)
            .decorate()
            .get()
            .toCompletableFuture()

    fun getNumberOfPagesWithFallback(isbn: String): CompletableFuture<Int?> =
        Decorators.ofCompletionStage { doGetNumberOfPages(isbn) }
            .withTimeLimiter(timeLimiter, scheduledExectioner)
            .withFallback { ex -> getNumberOfPagesFallback(isbn, ex) }
            .decorate()
            .get()
            .toCompletableFuture()

    private fun doGetNumberOfPages(isbn: String): CompletableFuture<Int?> =
        supplyAsync {
            // imagine an HTTP call here
            tripwire.possiblyWait()
            42
        }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}

@Configuration
class DownstreamServiceWithProgrammaticTimeLimiterConfiguration {

    @Bean
    fun downstreamServiceWithProgrammaticTimeLimiter(registry: TimeLimiterRegistry, tripwire: Tripwire) =
        DownstreamServiceWithProgrammaticTimeLimiter(
            scheduledExectioner = ScheduledThreadPoolExecutor(10),
            timeLimiter = registry.timeLimiter("downstream-service"),
            tripwire = tripwire
        )

}
