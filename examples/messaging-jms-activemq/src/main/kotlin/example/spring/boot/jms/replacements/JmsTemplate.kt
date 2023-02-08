package example.spring.boot.jms.replacements

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Duration.ofMillis
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Queue
import javax.jms.Session

class JmsTemplate(
    private val connectionFactory: ConnectionFactory,
    private val objectMapper: ObjectMapper,
) {

    fun convertAndSend(queueName: String, body: Any) =
        send(queueName) { session ->
            session.createTextMessage(objectMapper.writeValueAsString(body))
        }

    fun send(queueName: String, block: (Session) -> Message) =
        withQueueInSession(queueName) { session, queue ->
            session.produce(queue) {
                send(block(session))
            }
        }

    fun receive(queueName: String, timeout: Duration = ofMillis(0)): Message? =
        withQueueInSession(queueName) { session, queue ->
            session.consume(queue) {
                receive(timeout.toMillis())
            }
        }

    private fun <T> withQueueInSession(queueName: String, block: (Session, Queue) -> T): T =
        doWithSession { session -> block(session, session.createQueue(queueName)) }

    private fun <T> doWithSession(block: (Session) -> T): T {
        val connection = connectionFactory.createConnection()
        try {
            val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE) // TODO configurable
            try {
                return block(session)
            } finally {
                session.close()
            }
        } finally {
            connection.close()
        }
    }

    private fun Session.produce(queue: Queue, block: MessageProducer.() -> Unit) {
        val producer = createProducer(queue)
        try {
            block(producer)
        } finally {
            producer.close()
        }
    }

    private fun Session.consume(queue: Queue, block: MessageConsumer.() -> Message?): Message? {
        val consumer = createConsumer(queue)
        try {
            return block(consumer)
        } finally {
            consumer.close()
        }
    }

}
