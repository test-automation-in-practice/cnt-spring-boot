package provider.core

import java.util.*


interface MovieDataStore {

    fun getAll(): Set<MovieRecord>

    fun create(movie: Movie): MovieRecord

    fun createOrUpdate(id: UUID, movie: Movie): MovieRecord

    fun getById(id: UUID): MovieRecord?

}
