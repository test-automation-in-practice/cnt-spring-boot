package provider.persistence

import org.springframework.stereotype.Service
import provider.core.Movie
import provider.core.MovieDataStore
import provider.core.MovieRecord
import java.util.*


@Service
class InMemoryMovieDataStore : MovieDataStore {

    private val movies = HashMap<UUID, Movie>()

    override fun create(movie: Movie): MovieRecord {
        val id = UUID.randomUUID()
        movies[id] = movie
        return MovieRecord(id, movie)
    }

    override fun createOrUpdate(id: UUID, movie: Movie): MovieRecord {
        movies[id] = movie
        return MovieRecord(id, movie)
    }

    override fun getAll(): Set<MovieRecord> {
        return movies.entries
                .map { entry -> MovieRecord(entry.key, entry.value) }
                .toSet()
    }

    override fun getById(id: UUID): MovieRecord? {
        return movies[id]?.let { MovieRecord(id, it) }
    }

}
