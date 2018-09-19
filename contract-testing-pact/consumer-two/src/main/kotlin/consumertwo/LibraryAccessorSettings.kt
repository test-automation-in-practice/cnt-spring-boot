package consumertwo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("services.library")
class LibraryAccessorSettings {
    lateinit var url: String
}
