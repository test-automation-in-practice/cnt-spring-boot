package amqp.books

interface BookEventDispatcher {
    fun dispatch(event: BookEvent)
}