package example.spring.boot.resilience.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.kotlin.bulkhead.BulkheadConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import java.time.Duration
import java.util.concurrent.CompletableFuture

@TestInstance(PER_METHOD) // this way no reset mechanic is needed
class DownstreamServiceWithProgrammaticBulkheadTests {

    val cut = DownstreamServiceWithProgrammaticBulkhead(
        bulkhead = Bulkhead.of(
            /* name = */ "downstream-service",
            /* config = */ BulkheadConfig {
                maxConcurrentCalls(5)
                maxWaitDuration(Duration.ofMillis(1))
            }
        )
    )

    val isbn = "978-1804941836"

    @Test
    fun `sequential calls always return the result`() {
        repeat(10) {
            assertThat(cut.getNumberOfPages(isbn)).isEqualTo(42)
        }
    }

    @Test
    fun `parallel calls return the result as long as they don't exceed the threshold`() {
        val results = (1..5)
            .map { CompletableFuture.supplyAsync { cut.getNumberOfPages(isbn) } }
            .map(CompletableFuture<Int?>::get)
        assertThat(results).containsOnly(42)
    }

    @Test
    fun `without fallback an exception is thrown when the threshold is reached`() {
        val results = (1..10)
            .map {
                CompletableFuture.supplyAsync { cut.getNumberOfPages(isbn) }
                    .exceptionally { ex ->
                        when (ex.cause) {
                            is BulkheadFullException -> -1
                            else -> throw ex
                        }
                    }
            }
            .map(CompletableFuture<Int?>::get)
        assertThat(results).containsOnly(42, -1)
    }

    @Test
    fun `with fallback when the threshold is reached returns the fallback`() {
        val results = (1..10)
            .map { CompletableFuture.supplyAsync { cut.getNumberOfPagesWithFallback(isbn) } }
            .map(CompletableFuture<Int?>::get)
        assertThat(results).containsOnly(42, null)
    }

}
