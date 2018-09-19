package consumerone

import org.springframework.hateoas.ResourceSupport


data class Book(
        val isbn: String,
        val title: String,
        val authors: List<String>?
) : ResourceSupport()