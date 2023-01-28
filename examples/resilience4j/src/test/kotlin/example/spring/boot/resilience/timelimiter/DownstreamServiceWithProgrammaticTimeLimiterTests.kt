package example.spring.boot.resilience.timelimiter

import example.spring.boot.resilience.Tripwire
import io.github.resilience4j.kotlin.timelimiter.TimeLimiterConfig
import io.github.resilience4j.timelimiter.TimeLimiter
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeoutException

@TestInstance(PER_METHOD) // this way no reset mechanic is needed
class DownstreamServiceWithProgrammaticTimeLimiterTests {

    val tripwire: Tripwire = mockk(relaxUnitFun = true)
    val cut = DownstreamServiceWithProgrammaticTimeLimiter(
        scheduledExectioner = ScheduledThreadPoolExecutor(10),
        timeLimiter = TimeLimiter.of(
            /* name = */ "downstream-service",
            /* timeLimiterConfig = */ TimeLimiterConfig {
                timeoutDuration(Duration.ofMillis(100))
                cancelRunningFuture(true)
            }
        ),
        tripwire = tripwire
    )

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
