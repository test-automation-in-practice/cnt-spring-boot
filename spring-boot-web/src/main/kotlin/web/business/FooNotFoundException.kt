package web.business

import java.util.*

class FooNotFoundException(val id: UUID) : RuntimeException("Foo [$id] not found!")