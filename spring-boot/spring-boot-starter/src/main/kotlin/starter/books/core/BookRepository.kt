package starter.books.core

import java.util.UUID

interface BookRepository {

    /**
     * Inserts or updates the given record in the underlying data store.
     *
     * The [BookRecord]'s timestamp is updated to reflect the exact point in
     * time, that the record's data is from.
     *
     * @return the saved [BookRecord] with the updated timestamp
     */
    fun save(record: BookRecord): BookRecord

    /**
     * Tries to find the [BookRecord] with the given ID in the underlying data store.
     *
     * @return the [BookRecord] if one was found, otherwise `null`
     */
    fun findById(id: UUID): BookRecord?

    /**
     * Deletes the [BookRecord] with the given ID from the underlying data store.
     *
     * @return `true` if it was actually delete - `false` if there was no record
     *         with the given ID.
     */
    fun deleteById(id: UUID): Boolean

}
