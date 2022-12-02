package example.spring.boot.async

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.async.config.AsyncConfiguration
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.lang.Thread.currentThread
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS

internal class FooServiceTests {

    private val timeout = 1_000L

    @Nested
    inner class FunctionalityTests {

        private val barService: BarService = mockk(relaxUnitFun = true)
        private val cut = FooService(barService)

        @Test
        fun `invoking foo delegates to bar`() {
            cut.triggerDoingSomething()
            verify { barService.doSomething() }
        }

    }

    /// #####
    /// GOAL OF THE INTEGRATION TESTS: verify that a call to FooService is actually executed asynchronously
    /// #####

    @Nested
    @MockkBean(BarService::class)
    @SpringBootTest(classes = [FooService::class, AsyncConfiguration::class])
    inner class ImplicitIntegrationTests(
        @Autowired val fooService: FooService,
        @Autowired val barService: BarService,
    ) {

        /**
         * Implicit #1
         *
         * If the delegate service throws an exception and that exception does not reach the test.
         * Then the invocation _might_ have been asynchronous.
         *
         * - Relies on knowledge of the code - very white box.
         * - The same behaviour, from a blackbox perspective, could be explained by a try-catch.
         */
        @Test
        fun `invocation is async - delegate throws exception which does not pass through to the test`() {
            every { barService.doSomething() } throws RuntimeException("oops")
            fooService.triggerDoingSomething()
            verify(timeout = timeout) { barService.doSomething() }
        }

        /**
         * Implicit #2
         *
         * If the delegate service is invoked from another thread than the test, the invocation was
         * done asynchronously.
         *
         * - Relies less on knowledge of the code than the exception aproach.
         * - The same behaviour, from a blackbox perspective, could be explained by just that internal
         * invocation being done asynchronously.
         * - Still not guaranteed that the FooService.doSomething() method is completely asynchronous.
         * - Using MockK answers to extract Thread information is not easy to understand.
         */
        @Test
        fun `invocation is async - comparing thread names of test and invocation within the target method`() {
            val testThread = getThreadName()
            var invocationThread: String? = null
            val latch = CountDownLatch(1)

            every { barService.doSomething() } answers { invocationThread = getThreadName(); latch.countDown(); Unit }

            fooService.triggerDoingSomething()
            latch.await(timeout, MILLISECONDS)

            assertThat(invocationThread).isNotBlank().isNotEqualTo(testThread)
        }

    }

    @Nested
    @SpringBootTest(classes = [ExplicitIntegrationTestsConfiguration::class])
    inner class ExplicitIntegrationTests(
        @Autowired val fooService: FooService,
        @Autowired val stateHolder: ExplicitIntegrationTestsConfiguration
    ) {

        @BeforeEach
        fun resetStateHolder() = stateHolder.reset()

        /**
         * Initializing the FooService bean as a mock using a bean factory method
         * allows us to define behaviour while still autowiring the async-executing
         * proxy into our test.
         *
         * We need to store the invocation thread name somewhere, the configuration
         * class is one simple possibility.
         *
         * - Does not rely on knowledge about the implementation - method signature is enough.
         * - Guarantees that the whole method is invoked in another Thread -> @Async is used.
         * - Setup is much more complicated than implicit tests.
         * - Using MockK answers to extract Thread information is still not easy to understand.
         */
        @Test
        fun `invocation is async - actual service is executed using the async proxy`() {
            val testThread = getThreadName()

            fooService.triggerDoingSomething()
            stateHolder.latch.await(timeout, MILLISECONDS)

            assertThat(stateHolder.invocationThreadName).isNotBlank().isNotEqualTo(testThread)
        }

    }

    @Import(AsyncConfiguration::class)
    class ExplicitIntegrationTestsConfiguration {

        var latch = CountDownLatch(1)
        var invocationThreadName: String? = null

        fun reset() {
            latch = CountDownLatch(1)
            invocationThreadName = null
        }

        @Bean
        fun fooServiceMock(): FooService = mockk {
            every { triggerDoingSomething() }
                .answers { invocationThreadName = getThreadName(); latch.countDown(); Unit }
        }

    }

}

private fun getThreadName(): String = currentThread().name
