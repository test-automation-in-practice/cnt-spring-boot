package example.spring.boot.resilience.ratelimiter

import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [DownstreamServiceWithAnnotationBasedRateLimiterTestsConfiguration::class])
class DownstreamServiceWithAnnotationBasedRateLimiterTests(
    @Autowired val registry: RateLimiterRegistry,
    @Autowired val cut: DownstreamServiceWithAnnotationBasedRateLimiter
) {

    val isbn = "978-1804941836"

    @BeforeEach
    fun waitForRefresh() {
        registry.rateLimiter("downstream-service").drainPermissions()
        Thread.sleep(125) // 100ms refresh period + 25ms buffer
    }

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

@EnableAspectJAutoProxy
@ImportAutoConfiguration(RateLimiterAutoConfiguration::class)
@Import(DownstreamServiceWithAnnotationBasedRateLimiter::class)
private class DownstreamServiceWithAnnotationBasedRateLimiterTestsConfiguration
