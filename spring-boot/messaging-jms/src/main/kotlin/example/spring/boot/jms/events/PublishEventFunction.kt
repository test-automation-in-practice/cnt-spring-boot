package example.spring.boot.jms.events

import example.spring.boot.jms.business.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}
