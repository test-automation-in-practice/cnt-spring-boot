package example.spring.boot.caching.gateways.openlibrary

import java.io.IOException

interface OpenLibraryClient {

    @Throws(IOException::class)
    fun getNumberOfPages(isbn: String): Int?

}
