package example.spring.boot.webmvc.api

import jakarta.validation.constraints.Pattern

data class CreateBookRequest(
    val title: String,
    @field:Pattern(regexp = "[0-9]{13}")
    val isbn: String
)
