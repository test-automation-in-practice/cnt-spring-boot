package example.spring.boot.resilience.timelimiter

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.springboot3.timelimiter.autoconfigure.TimeLimiterAutoConfiguration
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.lang.Thread.sleep
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

@ActiveProfiles("test")
@MockkBean(Tripwire::class, relaxUnitFun = true)
@SpringBootTest(classes = [DownstreamServiceWithAnnotationBasedTimeLimiterTestsConfiguration::class])
class DownstreamServiceWithAnnotationBasedTimeLimiterTests(
    @Autowired val tripwire: Tripwire,
    @Autowired val cut: DownstreamServiceWithAnnotationBasedTimeLimiter
) {

    val isbn = "978-1804941836"

    @Test
    fun `returns result if operation takes less than threshold`() {
        val result = cut.getNumberOfPages(isbn)
        assertThat(result.get()).isEqualTo(42)
    }

    @Test
    fun `without fallback a timeout exception is thrown if operation takes longer than threshold`() {
        every { tripwire.possiblyWait() } answers { sleep(1_000) }

        val ex = assertThrows<ExecutionException> {
            cut.getNumberOfPages(isbn).get()
        }
        assertThat(ex).hasCauseInstanceOf(TimeoutException::class.java)
    }

    @Test
    fun `with fallback that fallback is returned if operation takes longer than threshold`() {
        every { tripwire.possiblyWait() } answers { sleep(1_000) }

        val result = cut.getNumberOfPagesWithFallback(isbn)
        assertThat(result.get()).isNull()
    }

}

@EnableAspectJAutoProxy
@ImportAutoConfiguration(TimeLimiterAutoConfiguration::class)
@Import(DownstreamServiceWithAnnotationBasedTimeLimiter::class, Tripwire::class)
private class DownstreamServiceWithAnnotationBasedTimeLimiterTestsConfiguration
