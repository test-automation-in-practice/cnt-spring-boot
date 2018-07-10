package web.api

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation
import java.time.OffsetDateTime

@Relation(
        value = "foo",
        collectionRelation = "foos"
)
data class FooResource(
        val bar: String,
        val xur: OffsetDateTime
) : ResourceSupport()