package example.spring.boot.micrometer.metrics.counter

import io.micrometer.core.aop.CountedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class CounterConfiguration {

    @Bean
    fun countedAspect(registry: MeterRegistry) = CountedAspect(registry)

}
