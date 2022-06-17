package example.spring.boot.advanced.e2e.domain

import example.spring.boot.advanced.e2e.domain.Examples.book_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.book_bobiverse2
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse2
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse3
import example.spring.boot.advanced.e2e.domain.Examples.record_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.record_bobiverse2
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BookCollectionTests {

    private val catalogue: BookCatalogue = mockk()
    private val repository: BookRecordRepository = mockk()
    private val mediaCollection: MediaCollection = mockk(relaxUnitFun = true)

    private val cut = BookCollection(catalogue, repository, mediaCollection)

    @Test
    fun `creates and returns book record if book was found`() {
        every { catalogue.findByIsbn(isbn_bobiverse1) } returns book_bobiverse1
        every { repository.create(book_bobiverse1) } returns record_bobiverse1

        val actual = addBookByIsbn(isbn_bobiverse1)

        assertThat(actual).isEqualTo(record_bobiverse1)
    }

    @Test
    fun `created book records are registered at the media collection`() {
        every { catalogue.findByIsbn(isbn_bobiverse2) } returns book_bobiverse2
        every { repository.create(book_bobiverse2) } returns record_bobiverse2

        addBookByIsbn(isbn_bobiverse2)

        verify { mediaCollection.register(record_bobiverse2) }
        // verify(timeout = 1_000) { mediaCollection.register(record_bobiverse2) }
    }

    @Test
    fun `returns failure if book was not found`() {
        every { catalogue.findByIsbn(isbn_bobiverse3) } returns null

        assertThrows<BookDataNotFoundException> { addBookByIsbn(isbn_bobiverse3) }

        confirmVerified(repository, mediaCollection)
    }

    private fun addBookByIsbn(isbn: String) = cut.addBookByIsbn(isbn).getOrThrow()

}
