package example.spring.boot.micrometer.metrics.timer

import example.spring.boot.micrometer.metrics.timer.TimerConfigurationTests.TestComponent
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component

@Import(TestComponent::class, SimpleMeterRegistry::class)
@SpringBootTest(classes = [TimerConfiguration::class])
class TimerConfigurationTests(
    @Autowired private val component: TestComponent,
    @Autowired private val registry: MeterRegistry
) {

    @Test
    fun `@Timed is enabled and method invocations are counted`() {
        repeat(9) { component.doSomething() }
        repeat(3) { component.doSomethingElse() }

        registry.forEachMeter { println("${it.id}") }

        assertThat(getCounterValue("doSomething")).isEqualTo(9)
        assertThat(getCounterValue("doSomethingElse")).isEqualTo(3)
    }

    private fun getCounterValue(methodName: String) =
        registry.find("method.timed").tag("method", methodName).timer()?.count()?.toInt()

    @Component
    class TestComponent {

        @Timed
        fun doSomething() = Thread.sleep(10)

        @Timed
        fun doSomethingElse() = Thread.sleep(25)

    }
}
