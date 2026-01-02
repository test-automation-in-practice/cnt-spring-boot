package example.spring.boot.graphql.utils

import org.springframework.boot.webtestclient.autoconfigure.WebTestClientBuilderCustomizer
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

class RestDocsWebTestClientCustomizer(
    private val restDocumentation: RestDocumentationContextProvider
) : WebTestClientBuilderCustomizer {
    override fun customize(builder: WebTestClient.Builder) {
        builder.filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
    }
}
