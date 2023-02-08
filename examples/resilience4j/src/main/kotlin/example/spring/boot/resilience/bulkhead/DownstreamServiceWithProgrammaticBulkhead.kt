package example.spring.boot.resilience.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadRegistry
import io.github.resilience4j.decorators.Decorators
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class DownstreamServiceWithProgrammaticBulkhead(
    private val bulkhead: Bulkhead
) {

    private val log = getLogger(javaClass)

    fun getNumberOfPages(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withBulkhead(bulkhead)
            .decorate()
            .get()

    fun getNumberOfPagesWithFallback(isbn: String): Int? =
        Decorators.ofSupplier { doGetNumberOfPages(isbn) }
            .withBulkhead(bulkhead)
            .withFallback { ex -> getNumberOfPagesFallback(isbn, ex) }
            .decorate()
            .get()

    private fun doGetNumberOfPages(isbn: String): Int {
        // imagine an HTTP call here
        Thread.sleep(10)
        return 42
    }

    private fun getNumberOfPagesFallback(isbn: String, ex: Throwable): Int? {
        log.warn("retrieval of number of pages for ISBN [$isbn] failed - falling back to null", ex)
        return null
    }

}

@Configuration
class DownstreamServiceWithProgrammaticBulkheadConfiguration {

    @Bean
    fun downstreamServiceWithProgrammaticBulkhead(registry: BulkheadRegistry) =
        DownstreamServiceWithProgrammaticBulkhead(
            bulkhead = registry.bulkhead("downstream-service")
        )

}
