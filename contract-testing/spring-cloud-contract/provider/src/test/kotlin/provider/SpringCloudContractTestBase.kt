package provider

import org.springframework.http.HttpHeaders.encodeBasicAuth
import kotlin.text.Charsets.UTF_8

// Spring Cloud Contract DSL-based tests are using a callback method to generate a valid Basic-Auth header.

class SpringCloudContractTestBase : ContractTestBase() {

    fun validAuthHeader() = basicAuthHeaderFor("user")

    private fun basicAuthHeaderFor(username: String) =
        "Basic ${encodeBasicAuth(username, username.reversed(), UTF_8)}"

}
