package provider.core

import java.util.UUID
import java.util.stream.Stream

import org.springframework.stereotype.Service


@Service
class MovieDatabase(
        private val dataStore: MovieDataStore
) {

    fun add(movie: Movie): MovieRecord = dataStore.create(movie)
    fun findAll(): Sequence<MovieRecord> = dataStore.getAll().asSequence()
    fun findById(id: UUID): MovieRecord = dataStore.getById(id) ?: throw NotFoundException()

}
