package example.spring.modulith.utils

import org.springframework.test.context.ContextConfiguration

@Retention
@Target(AnnotationTarget.CLASS)
@ContextConfiguration(
    initializers = [
        MongoDBInitializer::class,
        PostgreSQLInitializer::class,
        RabbitMQInitializer::class
    ]
)
annotation class InitializeWithContainers
