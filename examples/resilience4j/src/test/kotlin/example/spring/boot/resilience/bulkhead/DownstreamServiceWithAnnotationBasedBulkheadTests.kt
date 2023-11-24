package example.spring.boot.resilience.bulkhead

import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.springboot3.bulkhead.autoconfigure.BulkheadAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
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

    private fun <T> supplyAsyncWithExecutor(block: () -> T): CompletableFuture<T> =
        supplyAsync(block, executor)
}

@EnableAspectJAutoProxy
@ImportAutoConfiguration(BulkheadAutoConfiguration::class)
@Import(DownstreamServiceWithAnnotationBasedBulkhead::class)
private class DownstreamServiceWithAnnotationBasedBulkheadTestsConfiguration
