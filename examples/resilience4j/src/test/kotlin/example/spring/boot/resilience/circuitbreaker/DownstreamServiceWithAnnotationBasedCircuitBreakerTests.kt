package example.spring.boot.resilience.circuitbreaker

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.resilience.Resilience4JConfiguration
import example.spring.boot.resilience.Tripwire
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.LogRecordAssertion.Companion.assertThat
import io.github.logrecorder.assertion.containsExactly
import io.github.logrecorder.junit5.RecordLoggers
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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
@MockkBean(Tripwire::class, relaxUnitFun = true)
@SpringBootTest(classes = [DownstreamServiceWithAnnotationBasedCircuitBreakerTestsConfiguration::class])
class DownstreamServiceWithAnnotationBasedCircuitBreakerTests(
    @Autowired val tripwire: Tripwire,
    @Autowired val cut: DownstreamServiceWithAnnotationBasedCircuitBreaker,
    @Autowired val registry: CircuitBreakerRegistry
) {

    val isbn = "978-1804941836"

    @BeforeEach
    fun reset() {
        registry.circuitBreaker("downstream-service").reset()
    }

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
    @RecordLoggers(DownstreamServiceWithAnnotationBasedCircuitBreaker::class)
    fun `with fallback any exception returns the fallback`(log: LogRecord) {
        every { tripwire.possiblyThrowException() } throws TestException()

        repeat(6) {
            assertThat(cut.getNumberOfPagesWithFallback(isbn)).isNull()
        }

        assertThat(log) containsExactly {
            repeat(5) { warn(exception = isInstanceOf(TestException::class)) }
            warn(exception = isInstanceOf(CallNotPermittedException::class))
        }
    }

    class TestException : RuntimeException("oops")

}

@ImportAutoConfiguration(CircuitBreakerAutoConfiguration::class)
@Import(Resilience4JConfiguration::class, DownstreamServiceWithAnnotationBasedCircuitBreaker::class)
private class DownstreamServiceWithAnnotationBasedCircuitBreakerTestsConfiguration
