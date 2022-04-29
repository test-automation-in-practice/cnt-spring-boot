package advanced.e2e.gateways.bookcatalogue

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("services.book-catalogue")
class BookCatalogueProperties(
    val host: String,
    val port: Int
)
