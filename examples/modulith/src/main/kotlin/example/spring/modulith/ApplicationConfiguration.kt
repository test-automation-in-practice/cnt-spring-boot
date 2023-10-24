package example.spring.modulith

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.util.IdGenerator
import org.springframework.util.JdkIdGenerator
import java.time.Clock

@EnableAsync
@Configuration
class ApplicationConfiguration {

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun idGenerator(): IdGenerator = JdkIdGenerator()

}
