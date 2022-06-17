package example.spring.boot.advanced.e2e.gateways.common

import java.net.URL

interface ServiceProperties {
    val protocol: String
    val host: String
    val port: Int

    fun url(path: String): URL {
        val absolutePath = if (path.startsWith('/')) path else "/$path"
        return URL("$protocol://$host:$port$absolutePath")
    }
}
