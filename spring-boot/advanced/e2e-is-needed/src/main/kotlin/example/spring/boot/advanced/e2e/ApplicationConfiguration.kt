package example.spring.boot.advanced.e2e

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.util.JdkIdGenerator

@Configuration
@Import(JdkIdGenerator::class)
class ApplicationConfiguration
