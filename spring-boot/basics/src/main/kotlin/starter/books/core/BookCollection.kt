package starter.books.core

import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.util.IdGenerator
import java.util.*

// This component has a number of different kinds of methods:

@Service
class BookCollection(
    private val idGenerator: IdGenerator,
    private val repository: BookRepository,
    private val eventPublisher: BookEventPublisher
) {

    private val log = getLogger(javaClass)

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
        log.info("trying to delete book with ID '$id'")
        if (repository.deleteById(id)) {
            log.debug("book with ID '$id' was deleted")
            eventPublisher.publish(BookRecordDeletedEvent(id))
        } else {
            log.debug("book with ID '$id' was not deleted")
        }
    }

}
