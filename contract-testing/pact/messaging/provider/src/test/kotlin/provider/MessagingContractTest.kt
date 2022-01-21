package provider

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.loader.PactFolder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import provider.books.Book
import provider.books.BookCreatedEvent
import java.util.UUID.randomUUID

@Provider("provider")
@PactFolder("src/test/pacts")
class MessagingContractTest {

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setupMessageTarget(context: PactVerificationContext) {
        context.target = MessageTestTarget()
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun testTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @PactVerifyProvider("a book-created event")
    fun aBookCreatedEvent() = generateMessageJson {
        val event = BookCreatedEvent(
            eventId = randomUUID(),
            bookId = randomUUID(),
            book = Book(title = "Clean Code", isbn = "9780132350884")
        )
        objectMapper.writeValueAsString(event)
    }

    /**
     * Wrapper to make sure exceptions are logged properly since Pact (at least in the version we use here)
     * obfuscates them behind a generic error message.
     */
    private fun generateMessageJson(body: () -> String) = try {
        body()
    } catch (e: Exception) {
        LoggerFactory.getLogger(this::class.java).error("Failed to generate message json:", e)
        throw e
    }
}
