package jpa.foo

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "Foo")
@Table(name = "foo")
data class FooEntity(
        @Id
        val id: UUID,
        val bar: String,
        val xur: Int
)