package example.spring.boot.micrometer.scheduling

import com.ninjasquad.springmockk.SpykBean
import example.spring.boot.micrometer.scheduling.FixedRateSelfSchedulingComponentTests.TestComponent
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.config.FixedRateTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.time.Duration
import java.time.Duration.ofMinutes
import java.time.Duration.ofSeconds

@SpykBean(TestComponent::class)
@SpringBootTest(classes = [SchedulingConfiguration::class])
internal class FixedRateSelfSchedulingComponentTests(
    @Autowired private val cut: FixedRateSelfSchedulingComponent
) {

    @Test
    fun `component registers itself based on its configuration`() {
        val task = verifySingleTaskRegistration()

        assertThat(task.initialDelayDuration).isEqualTo(ofMinutes(1))
        assertThat(task.intervalDuration).isEqualTo(ofSeconds(30))
    }

    private fun verifySingleTaskRegistration(): FixedRateTask {
        val slot = slot<ScheduledTaskRegistrar>()
        verify { cut.configureTasks(capture(slot)) }
        return slot.captured.fixedRateTaskList.single() as FixedRateTask
    }

    class TestComponent : FixedRateSelfSchedulingComponent {
        override val initialDelay: Duration = ofMinutes(1)
        override val interval: Duration = ofSeconds(30)
        override fun run() = Unit
    }

}
