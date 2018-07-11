package provider.core

import java.util.*

data class Movie(
        val title: String,
        val description: String,
        val releaseYear: Int,
        val imdbScore: Float,
        val metacriticScore: Float
)

data class MovieRecord(val id: UUID, val movie: Movie)
