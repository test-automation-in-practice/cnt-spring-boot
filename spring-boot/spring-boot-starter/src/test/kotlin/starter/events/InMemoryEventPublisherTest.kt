package starter.events

import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.context.ApplicationEventPublisher
import starter.Examples.id_cleanCode
import starter.Examples.record_cleanCode
import starter.books.core.BookRecordCreatedEvent
import starter.books.core.BookRecordDeletedEvent

internal class InMemoryEventPublisherTest {

    val delegate: ApplicationEventPublisher = mockk(relaxed = true)
    val cut = InMemoryEventPublisher(delegate)

    @BeforeEach
    fun resetMocks() {
        clearMocks(delegate)
    }

    @TestFactory
    fun `all book events are simply published as application events`() =
        listOf(
            BookRecordCreatedEvent(record_cleanCode),
            BookRecordDeletedEvent(id_cleanCode)
        ).map { event ->
            dynamicTest(event::class.simpleName) {
                cut.publish(event)
                verify { delegate.publishEvent(event) }
            }
        }

}
