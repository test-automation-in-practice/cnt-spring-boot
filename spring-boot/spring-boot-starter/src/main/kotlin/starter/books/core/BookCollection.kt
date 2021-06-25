package starter.books.core

import org.springframework.stereotype.Service
import org.springframework.util.IdGenerator
import java.util.UUID

// This component has a number of different kinds of methods:

@Service
class BookCollection(
    private val idGenerator: IdGenerator,
    private val repository: BookRepository,
    private val eventPublisher: BookEventPublisher
) {

    // a method that takes an input and produces an output, but also has a side effect

    fun add(book: Book): BookRecord {
        val record = BookRecord(idGenerator.generateId(), book)
        val persistedRecord = repository.save(record)
        eventPublisher.publish(BookRecordCreatedEvent(persistedRecord))
        return persistedRecord
    }

    // a method that simply delegates to a less abstract component

    fun get(id: UUID): BookRecord? = repository.findById(id)

    // a method whose behaviour is determined by a dependency and that does not return anything

    fun delete(id: UUID) {
        if (repository.deleteById(id)) {
            eventPublisher.publish(BookRecordDeletedEvent(id))
        }
    }

}
