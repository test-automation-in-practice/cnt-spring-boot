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
class ExternallyScheduledTask(
    private val someService: SomeService,
    private val someOtherService: SomeOtherService,
) : Runnable {

    override fun run() {
        val value = someService.getSomething()
        someOtherService.doSomething(value)
    }

}

@Configuration
@EnableConfigurationProperties(ExternallyScheduledTaskProperties::class)
class ExternallyScheduledTaskConfiguration(
    private val properties: ExternallyScheduledTaskProperties,
    private val task: ExternallyScheduledTask
) : SchedulingConfigurer {

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        if (properties.enabled) {
            taskRegistrar.addFixedRateTask(IntervalTask(task, properties.rate, properties.initialDelay))
        }
    }

}

@ConfigurationProperties("tasks.externally-scheduled")
data class ExternallyScheduledTaskProperties(
    val enabled: Boolean,
    val rate: Duration,
    val initialDelay: Duration
)
