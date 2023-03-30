package example.spring.boot.scheduling.tasks

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import example.spring.boot.scheduling.config.SchedulingConfiguration
import example.spring.boot.scheduling.services.SomeOtherService
import example.spring.boot.scheduling.services.SomeService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.Thread.sleep

class AnnotationScheduledTaskTests {

    @Nested
    inner class FunctionTests {

        /*
         Whatever the task actually does should be tested on a unit level. This way the complexity of the task being
         executed automatically during the test is removed from consideration.
         */

        val someService: SomeService = mockk()
        val someOtherService: SomeOtherService = mockk()
        val cut = AnnotationScheduledTask(someService, someOtherService)

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
    @SpringBootTest(
        classes = [SchedulingConfiguration::class],
        properties = [
            "tasks.annotation.enabled=true",
            "tasks.annotation.rate=PT0.033S", // 33ms
            "tasks.annotation.initial-delay=PT0S", // immediately
        ]
    )
    @SpykBean(AnnotationScheduledTask::class) // TODO why does @MockkBean not work for this?
    @MockkBean(SomeService::class, SomeOtherService::class)
    inner class SchedulingIntegrationTests(
        @Autowired val someService: SomeService,
        @Autowired val someOtherService: SomeOtherService,
        @Autowired val cut: AnnotationScheduledTask,
    ) {

        @BeforeEach
        fun setupDefaultBehaviour() {
            every { someService.getSomething() } returns null
            every { someOtherService.doSomething(any()) } just runs
        }

        @Test
        fun `task is scheduled at configured rate`() {
            clearMocks(cut) // reset tracking
            sleep(100)
            verify(atLeast = 3) { cut.run() }
        }

    }

}
