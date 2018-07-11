package springbootamqp.foo

interface FooEventDispatcher {
    fun dispatch(event: FooEvent)
}