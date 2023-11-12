package example.spring.boot.resilience.circuitbreaker

import example.spring.boot.resilience.Tripwire
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.junit5.RecordLoggers
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.junit.jupiter.api.assertThrows
import java.time.Duration

@TestInstance(PER_METHOD) // this way no reset mechanic is needed
class DownstreamServiceWithProgrammaticCircuitBreakerTests {

    val tripwire: Tripwire = mockk(relaxUnitFun = true)
    val cut = DownstreamServiceWithProgrammaticCircuitBreaker(
        circuitBreaker = CircuitBreaker.of(
            /* name = */ "downstream-service",
            /* circuitBreakerConfig = */ CircuitBreakerConfig {
                failureRateThreshold(100f)
                waitDurationInOpenState(Duration.ofSeconds(1))
                slidingWindowSize(5)
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
    fun `without fallback exceptions are passed through until the threshold is reached`() {
        every { tripwire.possiblyThrowException() } throws TestException()

        repeat(5) {
            assertThrows<TestException> { cut.getNumberOfPages(isbn) }
        }
        assertThrows<CallNotPermittedException> { cut.getNumberOfPages(isbn) }
    }

    @Test
    @RecordLoggers(DownstreamServiceWithProgrammaticCircuitBreaker::class)
    fun `with fallback any exception returns the fallback`(log: LogRecord) {
        every { tripwire.possiblyThrowException() } throws TestException()

        repeat(6) {
            val actual = cut.getNumberOfPagesWithFallback(isbn)
            assertThat(actual).isNull()
        }

        assertThat(log) containsExactly {
            repeat(5) { warn(exception = isInstanceOf(TestException::class)) }
            warn(exception = isInstanceOf(CallNotPermittedException::class))
        }
    }

    class TestException : RuntimeException("oops")

}
