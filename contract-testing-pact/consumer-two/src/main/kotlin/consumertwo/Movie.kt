package consumertwo

import org.springframework.hateoas.ResourceSupport


class Movie(
        val title: String,
        val releaseYear: Int,
        val metacriticScore: Float
) : ResourceSupport()