package starter.books.enrichment

interface BookInformationSource {
    fun getBookInformation(isbn: String): BookInformation?
}
