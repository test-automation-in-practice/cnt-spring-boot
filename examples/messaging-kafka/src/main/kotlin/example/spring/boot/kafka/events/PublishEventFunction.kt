package example.spring.boot.kafka.events

import example.spring.boot.kafka.business.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}
