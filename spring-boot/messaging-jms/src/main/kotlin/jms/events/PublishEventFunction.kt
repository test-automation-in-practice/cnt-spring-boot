package jms.events

import jms.books.BookEvent

interface PublishEventFunction {
    operator fun invoke(event: BookEvent)
}
