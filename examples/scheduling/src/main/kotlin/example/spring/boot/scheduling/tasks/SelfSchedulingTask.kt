package example.spring.boot.scheduling.tasks

import example.spring.boot.scheduling.services.SomeOtherService
import example.spring.boot.scheduling.services.SomeService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.IntervalTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class SelfSchedulingTask(
    private val someService: SomeService,
    private val someOtherService: SomeOtherService,
    private val properties: SelfSchedulingTaskProperties,
) : Runnable, SchedulingConfigurer {

    override fun run() {
        val value = someService.getSomething()
        someOtherService.doSomething(value)
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        if (properties.enabled) {
            taskRegistrar.addFixedRateTask(IntervalTask(this, properties.rate, properties.initialDelay))
        }
    }

}

@Configuration
@EnableConfigurationProperties(SelfSchedulingTaskProperties::class)
class SelfSchedulingTaskConfiguration

@ConfigurationProperties("tasks.self-scheduling")
data class SelfSchedulingTaskProperties(
    val enabled: Boolean,
    val rate: Duration,
    val initialDelay: Duration
)
