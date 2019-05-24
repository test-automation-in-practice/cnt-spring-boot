package consumertwo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("services.library")
class LibraryAccessorSettings {
    lateinit var url: String
}
