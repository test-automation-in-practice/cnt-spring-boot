package example.spring.boot.advanced.e2e.gateways.bookcatalogue

import example.spring.boot.advanced.e2e.gateways.common.ServiceProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("services.book-catalogue")
class BookCatalogueServiceProperties(
    override val protocol: String,
    override val host: String,
    override val port: Int
) : ServiceProperties
