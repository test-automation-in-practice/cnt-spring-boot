package example.spring.boot.micrometer.metrics.counter

import example.spring.boot.micrometer.metrics.counter.CounterConfigurationTests.TestComponent
import io.micrometer.core.annotation.Counted
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component

@Import(TestComponent::class, SimpleMeterRegistry::class)
@SpringBootTest(classes = [CounterConfiguration::class])
class CounterConfigurationTests(
    @Autowired private val component: TestComponent,
    @Autowired private val registry: MeterRegistry
) {

    @Test
    fun `@Counted is enabled and method invocations are counted`() {
        repeat(9) { component.doSomething() }
        repeat(3) { component.doSomethingElse() }

        assertThat(getCounterValue("doSomething")).isEqualTo(9)
        assertThat(getCounterValue("doSomethingElse")).isEqualTo(3)
    }

    private fun getCounterValue(methodName: String) =
        registry.find("method.counted").tag("method", methodName).counter()?.count()?.toInt()

    @Component
    class TestComponent {

        @Counted
        fun doSomething() = Unit

        @Counted
        fun doSomethingElse() = Unit

    }
}
