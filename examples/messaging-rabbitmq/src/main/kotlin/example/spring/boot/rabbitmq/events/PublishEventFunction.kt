package example.spring.boot.rabbitmq.events

import example.spring.boot.rabbitmq.business.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}
