package starter.events

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import starter.books.core.BookEvent
import starter.books.core.BookEventPublisher

@Component
class InMemoryEventPublisher(
    private val delegate: ApplicationEventPublisher
) : BookEventPublisher {

    override fun publish(event: BookEvent) {
        delegate.publishEvent(event)
    }

}
