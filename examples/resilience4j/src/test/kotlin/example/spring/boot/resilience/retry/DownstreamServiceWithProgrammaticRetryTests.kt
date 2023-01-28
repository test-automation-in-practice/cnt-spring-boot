package example.spring.boot.resilience.retry

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.kotlin.retry.RetryConfig
import io.github.resilience4j.retry.Retry
import io.mockk.andThenJust
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.junit.jupiter.api.assertThrows
import java.time.Duration

@TestInstance(PER_METHOD) // this way no reset mechanic is needed
class DownstreamServiceWithProgrammaticRetryTests {

    val tripwire: Tripwire = mockk(relaxUnitFun = true)
    val cut = DownstreamServiceWithProgrammaticRetry(
        retry = Retry.of(
            /* name = */ "downstream-service",
            /* retryConfig = */ RetryConfig {
                maxAttempts(3)
                waitDuration(Duration.ofMillis(10))
            }
        ),
        tripwire = tripwire
    )

    val isbn = "978-1804941836"

    @Test
    fun `without an exception the result is returned`() {
        assertThat(cut.getNumberOfPages(isbn)).isEqualTo(42)
    }

    @Test
    fun `without fallback exception is passed through after max retries are reached`() {
        every { tripwire.possiblyThrowException() } throws TestException()

        assertThrows<TestException> { cut.getNumberOfPages(isbn) }

        verify(exactly = 3) { tripwire.possiblyThrowException() }
        confirmVerified(tripwire)
    }

    @Test
    fun `without fallback the result is returned if recovered before max retries are reached`() {
        every { tripwire.possiblyThrowException() }
            .throws(TestException()) // call #1/3
            .andThenThrows(TestException()) // call #2/3
            .andThenJust(runs) // call #3/3
            .andThenThrows(TestException()) // any more calls

        assertThat(cut.getNumberOfPages(isbn)).isEqualTo(42)

        verify(exactly = 3) { tripwire.possiblyThrowException() }
        confirmVerified(tripwire)
    }

    @Test
    fun `with fallback that fallback is returned after max retries are reached`() {
        every { tripwire.possiblyThrowException() } throws TestException()

        assertThat(cut.getNumberOfPagesWithFallback(isbn)).isNull()

        verify(exactly = 3) { tripwire.possiblyThrowException() }
        confirmVerified(tripwire)
    }

    class TestException : RuntimeException("oops")

}
