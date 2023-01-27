package example.spring.boot.advanced.e2e.gateways.mediacollection

import example.spring.boot.advanced.e2e.gateways.common.ServiceProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("services.media-collection")
class MediaCollectionServiceProperties(
    override val protocol: String,
    override val host: String,
    override val port: Int
) : ServiceProperties
