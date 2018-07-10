package web.business

import org.springframework.stereotype.Service
import java.util.*

@Service
class FooService {

    private val database = mutableMapOf<UUID, PersistedFoo>()

    fun create(foo: Foo): PersistedFoo {
        val id = UUID.randomUUID()
        val persistedFoo = PersistedFoo(id, foo)
        database[id] = persistedFoo
        return persistedFoo
    }

    fun delete(id: UUID) {
        database.remove(id) ?: throw FooNotFoundException(id)
    }

    fun get(id: UUID): PersistedFoo {
        return database[id] ?: throw FooNotFoundException(id)
    }

    fun getAll(): List<PersistedFoo> {
        return database.values.toList()
    }

}