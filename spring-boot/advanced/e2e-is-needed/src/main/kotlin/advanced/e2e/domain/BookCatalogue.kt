package advanced.e2e.domain

interface BookCatalogue {
    fun findByIsbn(isbn: String): Book?
}
