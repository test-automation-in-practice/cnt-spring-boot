package web.business

import java.time.OffsetDateTime
import java.util.*

data class Foo(
        val bar: String,
        val xur: OffsetDateTime
)

data class PersistedFoo(
        val id: UUID,
        val data: Foo
)