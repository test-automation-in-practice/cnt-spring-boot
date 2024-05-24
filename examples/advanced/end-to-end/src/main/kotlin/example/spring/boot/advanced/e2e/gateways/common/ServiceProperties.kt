package example.spring.boot.advanced.e2e.gateways.common

import java.net.URI
import java.net.URL

interface ServiceProperties {
    val protocol: String
    val host: String
    val port: Int

    fun url(path: String): URL {
        val absolutePath = if (path.startsWith('/')) path else "/$path"
        return URI.create("$protocol://$host:$port$absolutePath").toURL()
    }
}
