package example.spring.boot.kafka.messaging

import example.spring.boot.kafka.business.BookEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.retrytopic.RetryTopicSchedulerWrapper
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@EnableKafka
@Configuration
class KafkaConfiguration {

    @Bean
    fun errorHandlingDeserializer(): ErrorHandlingDeserializer<BookEvent> =
        ErrorHandlingDeserializer(JsonDeserializer())

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, BookEvent>): KafkaTemplate<String, BookEvent> =
        KafkaTemplate(producerFactory)

    /** https://github.com/spring-projects/spring-kafka/issues/2557 */
    @Bean
    fun workaroundRetryTopicSchedulerWrapper() =
        RetryTopicSchedulerWrapper(ThreadPoolTaskScheduler())
}
