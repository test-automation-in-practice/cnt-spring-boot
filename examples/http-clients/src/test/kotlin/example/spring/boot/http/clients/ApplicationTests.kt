package example.spring.boot.http.clients

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import example.spring.boot.http.clients.gateways.libraryservice.Book
import example.spring.boot.http.clients.gateways.libraryservice.CreatedBook
import example.spring.boot.http.clients.gateways.libraryservice.LibraryService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
internal class ApplicationTests(
    @Autowired val libraryService: LibraryService,
    @Autowired val wireMock: WireMockServer
) {

    @Test
    fun `uses default client to make call against auto configured WireMock`() {
        stub {
            post("/api/books")
                .withHeader(CONTENT_TYPE, containing(APPLICATION_JSON_VALUE))
                .withRequestBody(
                    equalToJson(
                        """
                        {
                          "title": "Clean Code",
                          "isbn": "9780132350884"
                        }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(
                            """
                            {
                              "id": "be64192f-879c-4346-8aff-76582117a42d",
                              "title": "Clean Code",
                              "isbn": "9780132350884"
                            }
                            """
                        )
                )
        }

        val book = Book(
            title = "Clean Code",
            isbn = "9780132350884"
        )
        val expected = CreatedBook(
            id = "be64192f-879c-4346-8aff-76582117a42d",
            title = "Clean Code",
            isbn = "9780132350884"
        )
        assertThat(libraryService.addBook(book)).isEqualTo(expected)
    }

    private fun stub(supplier: () -> MappingBuilder) {
        wireMock.addStubMapping(supplier().build())
    }

}
