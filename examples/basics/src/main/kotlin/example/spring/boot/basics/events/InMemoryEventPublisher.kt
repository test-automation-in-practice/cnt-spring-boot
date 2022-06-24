package example.spring.boot.basics.events

import example.spring.boot.basics.books.core.BookEvent
import example.spring.boot.basics.books.core.BookEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class InMemoryEventPublisher(
    private val delegate: ApplicationEventPublisher
) : BookEventPublisher {

    override fun publish(event: BookEvent) {
        delegate.publishEvent(event)
    }

}
