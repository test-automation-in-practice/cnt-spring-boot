package webflux.api

import javax.validation.constraints.Pattern

data class CreateBookRequest(
    val title: String,
    @field:Pattern(regexp = "[0-9]{13}")
    val isbn: String
)
