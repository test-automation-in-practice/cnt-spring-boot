package web.api

import java.time.OffsetDateTime
import javax.validation.constraints.Pattern

data class CreateFooRequest(
        @field:Pattern(regexp = "Hello .+!")
        val bar: String,
        val xur: OffsetDateTime
)