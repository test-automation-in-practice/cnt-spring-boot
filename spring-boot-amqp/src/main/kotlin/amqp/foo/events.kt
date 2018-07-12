package amqp.foo

import java.util.*

sealed class FooEvent {
    abstract val type: String
}

data class FooCreated(val id: UUID) : FooEvent() {
    override val type: String = "created"
}

data class FooDeleted(val id: UUID) : FooEvent() {
    override val type: String = "deleted"
}
