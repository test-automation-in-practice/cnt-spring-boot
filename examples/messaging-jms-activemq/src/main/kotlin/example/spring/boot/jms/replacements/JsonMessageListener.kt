package example.spring.boot.jms.replacements

import com.fasterxml.jackson.databind.ObjectMapper
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage
import kotlin.reflect.KClass

class JsonMessageListener<T : Any>(
    private val objectMapper: ObjectMapper,
    private val payloadType: KClass<T>,
    private val handler: (T) -> Unit
) : MessageListener {

    override fun onMessage(message: Message) {
        try {
            process(message)
        } catch (e: Exception) {
            // non RuntimeExceptions do not trigger retry / dead letter queue
            when (e) {
                is RuntimeException -> throw e
                else -> throw RuntimeException(e)
            }
        }
    }

    private fun process(message: Message) {
        val deserializedBody = when (message) {
            is TextMessage -> readTextMessage(message)
            else -> error("unsupported message type: ${message::class.qualifiedName}")
        }
        handler(deserializedBody)
    }

    private fun readTextMessage(message: TextMessage): T =
        objectMapper.readValue(message.text, payloadType.java)

}
