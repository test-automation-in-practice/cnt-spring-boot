package example.spring.boot.http.clients.gateways.libraryservice

interface LibraryService {

    @Throws(LibraryServiceException::class)
    fun addBook(book: Book): CreatedBook

}
