package mongodb.foo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface FooRepository : MongoRepository<FooDocument, UUID> {

    @Query("{ 'bar' : ?0 }")
    fun findByBar(bar: String): List<FooDocument>

}
