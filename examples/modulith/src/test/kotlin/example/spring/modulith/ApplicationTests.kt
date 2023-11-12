package example.spring.modulith

import example.spring.modulith.ApplicationTests.TestEventListener
import example.spring.modulith.utils.InitializeWithContainers
import io.github.logrecorder.api.LogRecord
import io.github.logrecorder.assertion.shouldContain
import io.github.logrecorder.junit5.RecordLoggers
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import org.springframework.modulith.moments.HourHasPassed
import org.springframework.modulith.moments.support.TimeMachine
import org.springframework.test.context.ActiveProfiles
import java.time.Duration.ofHours
import java.time.Duration.ofSeconds

@SpringBootTest
@ActiveProfiles("test")
@InitializeWithContainers
@Import(TestEventListener::class)
class ApplicationTests(
    @Autowired private val timeMachine: TimeMachine
) {

    @Test
    fun `application can be initialized`() {
        // nothing to check, for now
    }

    @Test // demo for moments, not a real test
    @RecordLoggers(TestEventListener::class)
    fun `produce and log 2 'hour has passed' moments events`(log: LogRecord) {
        timeMachine.shiftBy(ofHours(2) - ofSeconds(5))
        log shouldContain {
            info(startsWith("hour has passed: "))
            info(startsWith("hour has passed: "))
        }
    }

    @TestComponent
    class TestEventListener {

        private val log = LoggerFactory.getLogger(javaClass)

        @EventListener
        fun abc(event: HourHasPassed) {
            log.info("hour has passed: " + event.time.toString())
        }

    }

}
