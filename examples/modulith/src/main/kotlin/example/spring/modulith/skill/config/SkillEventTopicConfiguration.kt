package example.spring.modulith.skill.config

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

private const val SKILL_EVENTS_TOPIC = "skill-events"
private const val SKILL_EVENTS_QUEUE = "queues.events.skill"

@Configuration
class SkillEventTopicConfiguration {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun skillEventsTopic() = TopicExchange(SKILL_EVENTS_TOPIC)

    /** Binds a queue to the employee event topic to demonstrate that events are actually externalized. */
    @Bean
    fun skillBindings(skillEventsTopic: TopicExchange): Declarables {
        val queue = Queue(SKILL_EVENTS_QUEUE, true)
        val binding = BindingBuilder
            .bind(queue)
            .to(skillEventsTopic)
            .with("*")

        return Declarables(queue, binding)
    }

    /** Log each externalized event. */
    @RabbitListener(queues = [SKILL_EVENTS_QUEUE])
    fun handle(message: Message) {
        log.info("Message: ${message.body.toString(UTF_8)}; Properties: ${message.messageProperties}")
    }

}
