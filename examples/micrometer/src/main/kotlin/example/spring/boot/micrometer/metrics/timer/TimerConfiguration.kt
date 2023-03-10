package example.spring.boot.micrometer.metrics.timer

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class TimerConfiguration {

    @Bean
    fun timedAspect(registry: MeterRegistry) = TimedAspect(registry)

}
