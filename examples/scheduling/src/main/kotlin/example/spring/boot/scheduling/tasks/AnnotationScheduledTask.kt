package example.spring.boot.scheduling.tasks

import example.spring.boot.scheduling.services.SomeOtherService
import example.spring.boot.scheduling.services.SomeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("tasks.annotation.enabled", havingValue = "true")
class AnnotationScheduledTask(
    private val someService: SomeService,
    private val someOtherService: SomeOtherService,
) {

    @Scheduled(
        fixedRateString = "\${tasks.annotation.rate}",
        initialDelayString = "\${tasks.annotation.initial-delay}"
    )
    fun run() {
        val value = someService.getSomething()
        someOtherService.doSomething(value)
    }

}
