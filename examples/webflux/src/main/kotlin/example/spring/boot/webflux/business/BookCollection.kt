package example.spring.boot.webflux.business

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.just
import reactor.kotlin.core.publisher.toFlux
import java.util.UUID

@Service
class BookCollection {

    private val database = mutableMapOf<UUID, BookRecord>()

    fun add(book: Book): Mono<BookRecord> {
        val id = UUID.randomUUID()
        val bookRecord = BookRecord(id, book)
        database[id] = bookRecord
        return just(bookRecord)
    }

    fun delete(id: UUID): Mono<Unit> {
        database.remove(id)
        return empty()
    }

    // throws only to demonstrate exception handling in controller / tests
    // would normally simply return `BookRecord?`
    @Throws(BookRecordNotFoundException::class)
    fun get(id: UUID): Mono<BookRecord> {
        return database[id]?.let { just(it) } ?: error(BookRecordNotFoundException(id))
    }

    fun getAll(): Flux<BookRecord> {
        return database.values.toFlux()
    }

}
