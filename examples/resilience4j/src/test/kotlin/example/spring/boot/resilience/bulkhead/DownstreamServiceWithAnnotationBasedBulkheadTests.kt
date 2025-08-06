package example.spring.boot.resilience.bulkhead

import example.spring.boot.resilience.Resilience4JConfiguration
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.springboot3.bulkhead.autoconfigure.BulkheadAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors.newFixedThreadPool

@ActiveProfiles("test")
@SpringBootTest(classes = [DownstreamServiceWithAnnotationBasedBulkheadTestsConfiguration::class])
class DownstreamServiceWithAnnotationBasedBulkheadTests(
    @Autowired val cut: DownstreamServiceWithAnnotationBasedBulkhead
) {

    private val executor = newFixedThreadPool(10)
    private val isbn = "978-1804941836"

    @Test
    fun `sequential calls always return the result`() {
        repeat(10) {
            assertThat(cut.getNumberOfPages(isbn)).isEqualTo(42)
        }
    }

    @Test
    fun `parallel calls return the result as long as they don't exceed the threshold`() {
        val results = (1..5)
            .map { supplyAsyncWithExecutor { cut.getNumberOfPages(isbn) } }
            .map(CompletableFuture<Int?>::join)
        assertThat(results).containsOnly(42)
    }

    @Test
    fun `without fallback an exception is thrown when the threshold is reached`() {
        val results = (1..10)
            .map {
                supplyAsyncWithExecutor { cut.getNumberOfPages(isbn) }
                    .exceptionally { ex ->
                        when (ex.cause) {
                            is BulkheadFullException -> -1
                            else -> throw ex
                        }
                    }
            }
            .map(CompletableFuture<Int?>::join)
        assertThat(results).containsOnly(42, -1)
    }

    @Test
    fun `with fallback when the threshold is reached returns the fallback`() {
        val results = (1..10)
            .map { supplyAsyncWithExecutor { cut.getNumberOfPagesWithFallback(isbn) } }
            .map(CompletableFuture<Int?>::join)
        assertThat(results).containsOnly(42, null)
    }

    // Default CompletableFuture.supplyAsync(..) without an executor relies on the default ForkJoinPool.
    // That pool scales according to the available CPU cores.
    // This test would not run on our CI environment because we only have 1 available core.
    // Under those conditions, the bulkhead threshold would never be reached.
    private fun <T> supplyAsyncWithExecutor(block: () -> T): CompletableFuture<T> =
        supplyAsync(block, executor)
}

@ImportAutoConfiguration(BulkheadAutoConfiguration::class)
@Import(Resilience4JConfiguration::class, DownstreamServiceWithAnnotationBasedBulkhead::class)
private class DownstreamServiceWithAnnotationBasedBulkheadTestsConfiguration
