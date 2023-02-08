package example.spring.boot.jms.replacements

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory.getLogger
import java.lang.Thread.sleep
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.ExceptionListener
import javax.jms.JMSException
import javax.jms.MessageListener
import javax.jms.Session.AUTO_ACKNOWLEDGE

class MessageListenerContainer(
    private val connectionFactory: ConnectionFactory,
    private val messageListener: MessageListener,
    private val queueName: String,
    private val sessionTransacted: Boolean = false,
    private val sessionAcknowledgeMode: Int = AUTO_ACKNOWLEDGE,
) {

    private val log = getLogger(javaClass)
    private var connection: Connection? = null

    @PostConstruct
    fun start() {
        connection = connect()
    }

    private fun connect(): Connection {
        val connection = connectionFactory.createConnection()

        val session = connection.createSession(sessionTransacted, sessionAcknowledgeMode)
        val queue = session.createQueue(queueName)
        val consumer = session.createConsumer(queue)
        consumer.messageListener = messageListener

        connection.exceptionListener = ExceptionListener { ex ->
            log.error("Connection Exception: ${ex.message}", ex)
            handleConnectionLoss(ex)
        }
        connection.start()

        return connection
    }

    @PreDestroy
    fun stop() {
        try {
            connection?.close()
            connection = null
        } catch (e: JMSException) {
            log.error("error while closing the current connection: ${e.message}", e)
        }
    }

    private fun handleConnectionLoss(ex: JMSException) {
        stop()
        while (true) {
            if (tryToReConnect()) {
                break
            } else {
                sleep(1_000)
            }
        }
    }

    private fun tryToReConnect(): Boolean =
        try {
            connection = connect()
            log.info("re-established connection to message broker")
            true
        } catch (e: Exception) {
            log.error("failed to establish connection to message broker: ${e.message}", e)
            false
        }

}
