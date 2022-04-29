package advanced.e2e.gateways.mediacollection

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("services.media-collection")
class MediaCollectionProperties(
    val host: String,
    val port: Int
)
