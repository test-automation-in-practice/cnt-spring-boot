package example.spring.boot.scheduling.tasks

import example.spring.boot.scheduling.services.SomeOtherService
import example.spring.boot.scheduling.services.SomeService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.scheduling.config.IntervalTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.time.Duration.ofSeconds

class ExternallyScheduledTaskTests {

    @Nested
    inner class TaskTests {

        /*
         Whatever the task actually does should be tested on a unit level. This way the complexity of the task being
         executed automatically during the test is removed from consideration.
         */

        val someService: SomeService = mockk()
        val someOtherService: SomeOtherService = mockk()
        val cut = ExternallyScheduledTask(someService, someOtherService)

        @BeforeEach
        fun setupDefaultBehaviour() {
            every { someService.getSomething() } returns null
            every { someOtherService.doSomething(any()) } just runs
        }

        @Test
        fun `the task does what it should do`() {
            every { someService.getSomething() } returns "something"
            cut.run()
            verify { someOtherService.doSomething("something") }
        }

        // as many tests as needed to check functionality

    }

    @Nested
    inner class SchedulingTests {

        /*
         With the programmatic approach to scheduling, every aspect can be unit-tested.
         There is no real need to write a technology integration test. The wiring of the components
         is checked using an application-level smoke test anyway.
         */

        val task: ExternallyScheduledTask = mockk()
        val taskRegistrar: ScheduledTaskRegistrar = mockk()

        val scheduledTask = slot<IntervalTask>()

        @BeforeEach
        fun setupTaskCapturing() {
            every { taskRegistrar.addFixedRateTask(capture(scheduledTask)) } just runs
        }

        @Test
        fun `if enabled the task is scheduled with correct configuration`() {
            val cut = cut(enabled = true, rateInSeconds = 3, initialDelayInSeconds = 7)

            cut.configureTasks(taskRegistrar)

            with(scheduledTask.captured) {
                assertThat(intervalDuration).isEqualTo(ofSeconds(3))
                assertThat(initialDelayDuration).isEqualTo(ofSeconds(7))
            }
        }

        @Test
        fun `if disabled the task is not scheduled`() {
            val cut = cut(enabled = false)
            cut.configureTasks(taskRegistrar)
            confirmVerified(taskRegistrar) // nothing was registered
        }

        fun cut(enabled: Boolean, rateInSeconds: Long = 1, initialDelayInSeconds: Long = 1) =
            ExternallyScheduledTaskConfiguration(
                properties = ExternallyScheduledTaskProperties(
                    enabled = enabled,
                    rate = ofSeconds(rateInSeconds),
                    initialDelay = ofSeconds(initialDelayInSeconds)
                ),
                task = task
            )

    }

}
