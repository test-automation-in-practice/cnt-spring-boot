package advanced.e2e.gateways.bookcatalogue

import advanced.e2e.gateways.common.ServiceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("services.book-catalogue")
class BookCatalogueServiceProperties(
    override val protocol: String,
    override val host: String,
    override val port: Int
) : ServiceProperties
