package jdbc.foo

import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

@Service
class FooRepository(
        private val jdbcTemplate: NamedParameterJdbcTemplate,
        private val idGenerator: IdGenerator
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun create(foo: Foo): PersistedFoo {
        val id = idGenerator.generateId()
        return try {
            val query = "INSERT INTO foo (id, bar, xur) VALUES (:id, :bar, :xur)"
            val parameters = mutableMapOf(
                    "id" to id.toString(),
                    "bar" to foo.bar,
                    "xur" to foo.xur
            )
            jdbcTemplate.update(query, parameters)
            PersistedFoo(id, foo)
        } catch (e: DuplicateKeyException) {
            log.warn("ID collision occurred for ID [{}] - retrying with new ID", id)
            create(foo)
        }
    }

    fun update(persistedFoo: PersistedFoo) {
        val query = "UPDATE foo SET bar = :bar, xur = :xur WHERE id = :id"
        val parameters = mutableMapOf(
                "id" to persistedFoo.id.toString(),
                "bar" to persistedFoo.foo.bar,
                "xur" to persistedFoo.foo.xur
        )

        if (jdbcTemplate.update(query, parameters) == 0) {
            throw FooNotFoundException(persistedFoo.id)
        }
    }

    fun findBy(id: UUID): PersistedFoo? {
        val query = "SELECT * FROM foo WHERE id = :id"
        val parameters = mapOf("id" to id.toString())

        val rowMapper = RowMapper { rs, _ ->
            val bar = rs.getString("bar")!!
            val xur = rs.getInt("xur")
            PersistedFoo(id, Foo(bar, xur))
        }

        return try {
            jdbcTemplate.queryForObject(query, parameters, rowMapper)
        } catch (e: IncorrectResultSizeDataAccessException) {
            null
        }
    }

    fun deleteBy(id: UUID) {
        val query = "DELETE FROM foo WHERE id = :id"
        val parameters = mapOf("id" to id.toString())

        if (jdbcTemplate.update(query, parameters) == 0) {
            throw FooNotFoundException(id)
        }
    }

}

@Component
class IdGenerator {

    fun generateId(): UUID {
        return UUID.randomUUID()
    }

}