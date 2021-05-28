package caching.books

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class OpenLibraryAccessor(
    private val client: OpenLibraryClient
) {

    @Cacheable("getNumberOfPagesByIsbn")
    fun getNumberOfPages(isbn: String): Int? {
        return client.getNumberOfPages(isbn)
    }

}

interface OpenLibraryClient {
    fun getNumberOfPages(isbn: String): Int?
}

@Component
class IsdnSimulator : OpenLibraryClient {

    override fun getNumberOfPages(isbn: String): Int? {
        Thread.sleep(5_000)
        return 42
    }

}