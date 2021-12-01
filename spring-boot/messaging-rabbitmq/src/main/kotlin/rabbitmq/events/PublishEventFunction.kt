package rabbitmq.events

import rabbitmq.books.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}
