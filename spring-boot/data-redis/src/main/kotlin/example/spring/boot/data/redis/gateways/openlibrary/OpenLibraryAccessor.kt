package example.spring.boot.data.redis.gateways.openlibrary

import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class OpenLibraryAccessor(
    private val client: OpenLibraryClient
) {

    private val log = getLogger(javaClass)

    @Cacheable("getNumberOfPagesByIsbn", unless = "#result == null")
    fun getNumberOfPages(isbn: String): Int? =
        try {
            client.getNumberOfPages(isbn)
        } catch (e: IOException) {
            log.error("Unable to get number of pages for ISBN [$isbn] because of an exception: ${e.message}", e)
            null
        }

}
