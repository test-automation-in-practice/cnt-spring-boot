package consumerone

import org.springframework.hateoas.Resources
import org.springframework.http.HttpEntity.EMPTY
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*


@Service
class MoviesServiceGateway(
        private val restTemplate: RestTemplate,
        private val settings: MoviesServiceSettings
) {

    fun getMovies(): List<Movie> {
        val response =
                restTemplate.exchange("${settings.url}/movies", GET, EMPTY, MovieListResource::class.java)
        return when (response.statusCode) {
            OK -> ArrayList(response.body?.content ?: error("no response body"))
            else -> error("server responded with: $response")
        }
    }

    fun getMovie(id: String): Movie? {
        val response =
                restTemplate.exchange("${settings.url}/movies/$id", GET, EMPTY, Movie::class.java)
        return when (response.statusCode) {
            OK -> response.body
            NOT_FOUND -> null
            else -> error("server responded with: $response")
        }
    }

    class MovieListResource : Resources<Movie>()

}
