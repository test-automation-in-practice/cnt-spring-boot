package feign.gateways.library

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LibraryAccessor internal constructor(
        private val client: LibraryClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun addBook(title: String, isbn: String): Boolean {
        return try {
            client.post(Book(title, isbn))
            true
        } catch (e: FeignException) {
            log.error("failed to get message of the day from Bar Service", e)
            false
        }
    }

}