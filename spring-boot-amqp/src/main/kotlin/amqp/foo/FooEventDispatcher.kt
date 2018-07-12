package amqp.foo

interface FooEventDispatcher {
    fun dispatch(event: FooEvent)
}