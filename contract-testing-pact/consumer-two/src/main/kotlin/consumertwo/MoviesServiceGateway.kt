package consumertwo

import java.util.ArrayList
import java.util.Optional

import org.springframework.hateoas.Resources
import org.springframework.http.HttpEntity
import org.springframework.http.HttpEntity.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MoviesServiceGateway(
        private val restTemplate: RestTemplate,
        private val settings: MoviesServiceSettings
) {

    fun getMovies(): List<Movie> {
        val response =
                restTemplate.exchange("${settings.url}/movies", GET, EMPTY, MovieListResource::class.java)
        return when (response.statusCode) {
            HttpStatus.OK -> ArrayList(response.body?.content ?: error("no response body"))
            else -> error("server responded with: $response")
        }
    }

    fun getMovie(id: String): Movie? {
        val response =
                restTemplate.exchange("${settings.url}/movies/$id", GET, EMPTY, Movie::class.java)
        return when (response.statusCode) {
            HttpStatus.OK -> response.body
            HttpStatus.NOT_FOUND -> null
            else -> error("server responded with: $response")
        }
    }

    class MovieListResource : Resources<Movie>()

}