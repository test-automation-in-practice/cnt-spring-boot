package consumertwo

import org.springframework.http.HttpEntity.EMPTY
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class LibraryAccessor(
    private val restTemplate: RestTemplate,
    private val settings: LibraryAccessorSettings
) {

    fun getBook(id: String): Book? {
        val response =
            restTemplate.exchange("${settings.url}/books/$id", GET, EMPTY, Book::class.java)
        return when (response.statusCode) {
            OK -> response.body
            NOT_FOUND -> null
            else -> error("server responded with: $response")
        }
    }

}