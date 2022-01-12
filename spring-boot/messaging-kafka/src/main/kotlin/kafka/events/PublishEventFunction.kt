package kafka.events

import kafka.books.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}