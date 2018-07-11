package consumerone

import org.springframework.hateoas.ResourceSupport


data class Movie(
        val title: String?,
        val imdbScore: Float?
) : ResourceSupport()