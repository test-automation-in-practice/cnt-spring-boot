package example.spring.boot.advanced.e2e.gateways.bookcatalogue

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import example.spring.boot.advanced.e2e.domain.Examples.book_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse1
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse2
import example.spring.boot.advanced.e2e.domain.Examples.isbn_bobiverse3
import example.spring.boot.advanced.e2e.security.TEST_TOKEN_2
import example.spring.boot.advanced.e2e.security.clearSecurityContext
import example.spring.boot.advanced.e2e.security.setSecurityContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import java.io.IOException

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = [BookCatalogueClientTestConfiguration::class])
internal class BookCatalogueClientTests(
    @Autowired val cut: BookCatalogueClient,
    @Autowired val wireMock: WireMockServer
) {

    @AfterEach
    fun cleanup() {
        clearSecurityContext()
    }

    @Test
    fun `sends correct request and found response can be processed`() {
        setSecurityContext(TEST_TOKEN_2)

        stub {
            get("/api/books/$isbn_bobiverse1")
                .withHeader(AUTHORIZATION, equalTo("Bearer $TEST_TOKEN_2"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(
                            """
                            {
                              "isbn": "9781680680584",
                              "title": "We Are Legion (We Are Bob)"
                            } 
                            """
                        )
                )
        }

        val actual = cut.findByIsbn(isbn_bobiverse1)

        assertThat(actual).isEqualTo(book_bobiverse1)
    }

    @ParameterizedTest
    @ValueSource(ints = [204, 404])
    fun `not-found responses can be processed`(status: Int) {
        stub { get("/api/books/$isbn_bobiverse2").willReturn(aResponse().withStatus(status).withBody("")) }
        assertThat(cut.findByIsbn(isbn_bobiverse2)).isNull()
    }

    @ParameterizedTest
    @ValueSource(ints = [400, 401, 403, 500, 504])
    fun `bad server responses throw exception`(status: Int) {
        stub { get("/api/books/$isbn_bobiverse3").willReturn(aResponse().withStatus(status).withBody("oops")) }
        val ex = assertThrows<IOException> { cut.findByIsbn(isbn_bobiverse3) }
        assertThat(ex).hasMessage("Failed call [status=$status]: oops")
    }

    private fun stub(supplier: () -> MappingBuilder) {
        wireMock.givenThat(supplier())
    }
}

@Import(BookCatalogueClient::class)
@EnableConfigurationProperties(BookCatalogueServiceProperties::class)
private class BookCatalogueClientTestConfiguration
