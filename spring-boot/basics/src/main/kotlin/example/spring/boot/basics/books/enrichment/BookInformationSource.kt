package example.spring.boot.basics.books.enrichment

interface BookInformationSource {
    fun getBookInformation(isbn: String): BookInformation?
}
