package example.spring.boot.resilience.ratelimiter

import io.github.resilience4j.kotlin.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.junit.jupiter.api.assertThrows
import java.time.Duration

@TestInstance(PER_METHOD) // this way no reset mechanic is needed
class DownstreamServiceWithProgrammaticRateLimiterTests {

    val cut = DownstreamServiceWithProgrammaticRateLimiter(
        rateLimiter = RateLimiter.of(
            /* name = */ "downstream-service",
            /* rateLimiterConfig = */ RateLimiterConfig {
                limitForPeriod(10)
                limitRefreshPeriod(Duration.ofMillis(100))
                timeoutDuration(Duration.ofMillis(0))
            }
        )
    )

    val isbn = "978-1804941836"

    @Test
    fun `calling multiple times within threshold returns the result`() {
        repeat(5) {
            assertThat(cut.getNumberOfPages(isbn)).isEqualTo(42)
        }
    }

    @Test
    fun `without fallback an exception is thrown when calling more times than the threshold`() {
        assertThrows<RequestNotPermitted> {
            repeat(25) {
                cut.getNumberOfPages(isbn)
            }
        }
    }

    @Test
    fun `with fallback that fallback is returned when calling more times than the threshold`() {
        val results = (1..25)
            .map { cut.getNumberOfPagesWithFallback(isbn) }
            .toSet()
        assertThat(results).containsOnly(42, null)
    }

}
