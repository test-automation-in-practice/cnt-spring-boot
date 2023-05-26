package example.spring.boot.security.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import example.spring.boot.security.utils.ContainerizedKeycloakInitializer.Companion.container
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.ofString

private val client = HttpClient.newHttpClient()
private val objectMapper = jacksonObjectMapper()

fun getUserToken(): String = getToken("john.doe@example.com", "s3cret")
fun getCuratorToken(): String = getToken("jane.doe@example.com", "secr3t")

private fun getToken(username: String, password: String): String {
    val uri = "${container.authServerUrl}/realms/cnt-sb/protocol/openid-connect/token"
    val formData = mapOf(
        "grant_type" to "password",
        "client_id" to "cnt-sb-client",
        "username" to username,
        "password" to password
    )
    val body = formData.map { (key, value) -> "$key=$value" }.joinToString(separator = "&")

    val request = HttpRequest.newBuilder()
        .POST(ofString(body))
        .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
        .header(ACCEPT, APPLICATION_JSON_VALUE)
        .uri(URI(uri))
        .build()

    val response = client.send(request, ofString())
    val json = objectMapper.readTree(response.body())

    return json["access_token"]?.textValue() ?: error("no access token found in response: ${response.body()}")
}
