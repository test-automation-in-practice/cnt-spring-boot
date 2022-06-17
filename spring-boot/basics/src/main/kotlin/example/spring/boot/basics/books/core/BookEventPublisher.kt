package example.spring.boot.basics.books.core

interface BookEventPublisher {

    /**
     * Publishes the given [BookEvent] in a way that allows other party of the application
     * and even other applications to react to the events occurrence.
     */
    fun publish(event: BookEvent)

}
