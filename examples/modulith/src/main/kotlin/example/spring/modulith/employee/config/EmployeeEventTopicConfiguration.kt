package example.spring.modulith.employee.config

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.text.Charsets.UTF_8

private const val EMPLOYEE_EVENTS_TOPIC = "employee-events"
private const val EMPLOYEE_EVENTS_QUEUE = "queues.events.employee"

@Configuration
class EmployeeEventTopicConfiguration {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun employeeEventsTopic() = TopicExchange(EMPLOYEE_EVENTS_TOPIC)

    /** Binds a queue to the employee event topic to demonstrate that events are actually externalized. */
    @Bean
    fun employeeBindings(employeeEventsTopic: TopicExchange): Declarables {
        val queue = Queue(EMPLOYEE_EVENTS_QUEUE, true)
        val binding = BindingBuilder
            .bind(queue)
            .to(employeeEventsTopic)
            .with("*")

        return Declarables(queue, binding)
    }

    /** Log each externalized event. */
    @RabbitListener(queues = [EMPLOYEE_EVENTS_QUEUE])
    fun handle(message: Message) {
        log.info("Message: ${message.body.toString(UTF_8)}; Properties: ${message.messageProperties}")
    }

}
