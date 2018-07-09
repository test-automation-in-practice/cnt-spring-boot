package jdbc.foo

import java.util.*

class FooNotFoundException(id: UUID) : RuntimeException("The Foo with ID [$id] was not found in the database!")