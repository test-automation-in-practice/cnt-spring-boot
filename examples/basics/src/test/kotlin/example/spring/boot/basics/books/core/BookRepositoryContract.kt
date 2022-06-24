package example.spring.boot.basics.books.core

import example.spring.boot.basics.Examples
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

// This is a test contract for all BookRepository implementations.
// A test contract implements tests for behaviour that every implementation has to comply with.

interface BookRepositoryContract {

    val cut: BookRepository

    @Test
    fun `saving a book record updates its timestamp and returns its modified state`() {
        val actual = cut.save(Examples.record_cleanCode)
        val expected = Examples.record_cleanCode.copy(timestamp = now())
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `saved books can be got by their ID`() {
        val saved = cut.save(Examples.record_cleanCode)
        val got = cut.findById(Examples.id_cleanCode)
        assertThat(got).isEqualTo(saved)
    }

    @Test
    fun `saved books can be deleted`() {
        cut.save(Examples.record_cleanCode)
        assertThat(cut.findById(Examples.id_cleanCode)).isNotNull()
        assertThat(cut.deleteById(Examples.id_cleanCode)).isTrue()
        assertThat(cut.findById(Examples.id_cleanCode)).isNull()
    }

    @Test
    fun `unknown books are not deleted`() {
        assertThat(cut.deleteById(Examples.id_cleanCode)).isFalse()
    }

    fun now(): Instant

}
