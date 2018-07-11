package consumerone

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("services.movies")
class MoviesServiceSettings {
    lateinit var url: String
}
