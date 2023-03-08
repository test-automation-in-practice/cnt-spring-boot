package example.spring.boot.micrometer.scheduling

import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.FixedRateTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.time.Duration

interface FixedRateSelfSchedulingComponent : SchedulingConfigurer, Runnable {

    val initialDelay: Duration
    val interval: Duration

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val task = FixedRateTask(this, interval, initialDelay)
        taskRegistrar.addFixedRateTask(task)
    }

}
