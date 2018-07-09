package jdbc.foo

import java.util.*

data class Foo(val bar: String, val xur: Int)
data class PersistedFoo(val id: UUID, val foo: Foo)