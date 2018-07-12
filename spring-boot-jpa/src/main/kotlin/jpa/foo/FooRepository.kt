package jpa.foo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface FooRepository : JpaRepository<FooEntity, UUID> {

    @Query("SELECT f FROM Foo f WHERE f.bar = :bar")
    fun findByBar(bar: String): List<FooEntity>

}
