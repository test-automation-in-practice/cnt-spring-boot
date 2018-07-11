package provider.api

import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import provider.core.Movie
import provider.core.MovieDatabase
import provider.core.MovieRecord
import java.util.*


@RestController
@RequestMapping("/movies")
class MoviesController(private val database: MovieDatabase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody movie: Movie): Resource<Movie> {
        val record = database.add(movie)
        return toMovieResource(record)
    }

    @GetMapping
    fun get(): Resources<Resource<Movie>> {
        val movies = database.findAll()
                .map { this.toMovieResource(it) }
                .toList()
        val selfLink = linkTo(methodOn(MoviesController::class.java).get()).withSelfRel()
        return Resources(movies, selfLink)
    }

    @GetMapping("/{id}")
    operator fun get(@PathVariable id: UUID): Resource<Movie> {
        val record = database.findById(id)
        return toMovieResource(record)
    }

    private fun toMovieResource(movieRecord: MovieRecord): Resource<Movie> {
        val selfLink = linkTo(methodOn(MoviesController::class.java).get(movieRecord.id)).withSelfRel()
        return Resource(movieRecord.movie, selfLink)
    }

}
