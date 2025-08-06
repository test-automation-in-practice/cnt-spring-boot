package example.spring.boot.resilience.retry

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.resilience.Resilience4JConfiguration
import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration
import io.mockk.andThenJust
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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
@SpringBootTest(classes = [DownstreamServiceWithAnnotationBasedRetryTestsConfiguration::class])
class DownstreamServiceWithAnnotationBasedRetryTests(
    @Autowired val tripwire: Tripwire,
    @Autowired val cut: DownstreamServiceWithAnnotationBasedRetry
) {

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

@ImportAutoConfiguration(RetryAutoConfiguration::class)
@Import(Resilience4JConfiguration::class, DownstreamServiceWithAnnotationBasedRetry::class)
private class DownstreamServiceWithAnnotationBasedRetryTestsConfiguration
