package webflux.api.default

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import webflux.business.BookCollection
import webflux.business.BookRecordNotFoundException
import webflux.business.Examples.book_cleanCode
import webflux.business.Examples.id_cleanArchitecture
import webflux.business.Examples.id_cleanCode
import webflux.business.Examples.record_cleanArchitecture
import webflux.business.Examples.record_cleanCode
import java.util.*

private class DefaultBookControllerTestConfiguration {
    @Bean
    fun bookCollection(): BookCollection = mockk()
}

@WebFluxTest(DefaultBooksController::class)
@Import(DefaultBookControllerTestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/default/books")
internal class DefaultBookControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val webTestClient: WebTestClient
) {

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    @Nested
    @DisplayName("POST /default-api/books")
    inner class Post {

        fun postBook(body: String) = webTestClient.post()
            .uri("/default-api/books")
            .contentType(APPLICATION_JSON)
            .bodyValue(body)
            .exchange()

        @Test
        fun `posting a book adds it to the library and returns resource representation`() {
            every { bookCollection.add(book_cleanCode) } returns Mono.just(record_cleanCode)

            postBook("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
                .expectStatus().isCreated
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().json {
                    """
                    {
                        "id": "$id_cleanCode",
                        "title": "Clean Code",
                        "isbn": "9780132350884"
                    }
                    """
                }
                .andDocument("post/created")
        }

        @Test
        fun `posting a book with a invalid property responds with status 400`() {
            postBook("""{ "title": "Clean Code", "isbn": "0132350884" }""") // isbn malformed
                .expectStatus().isBadRequest
                .expectBody()
                .andDocument("post/bad-request_invalid")
        }

        @Test
        fun `posting a book with missing property responds with status 400`() {
            postBook("""{ "isbn": "Clean Code" }""")
                .expectStatus().isBadRequest
                .expectBody()
                .andDocument("post/bad-request_missing")
        }

    }

    @Nested
    @DisplayName("GET /default-api/books")
    inner class Get {

        fun getBooks() = webTestClient.get()
            .uri("/default-api/books")
            .exchange()

        @Test
        fun `getting all books returns their resource representation as a resource list`() {
            every { bookCollection.getAll() } returns Flux.just(record_cleanCode, record_cleanArchitecture)

            getBooks()
                .expectStatus().isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().json {
                    """
                    [
                        {
                            "id": "$id_cleanCode",
                            "title": "Clean Code",
                            "isbn": "9780132350884"
                        },
                        {
                            "id": "$id_cleanArchitecture",
                            "title": "Clean Architecture",
                            "isbn": "9780134494166"
                        }
                    ]
                    """
                }
                .andDocument("get/ok_found")
        }

        @Test
        fun `getting all books when there are none returns empty resource list`() {
            every { bookCollection.getAll() } returns Flux.empty()

            getBooks()
                .expectStatus().isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().json("[]")
                .andDocument("get/ok_empty")
        }

    }

    @Nested
    @DisplayName("GET /default-api/books/{id}")
    inner class GetById {

        fun getBook(id: UUID) = webTestClient.get()
            .uri("/default-api/books/$id")
            .exchange()

        @Test
        fun `getting a book by its id returns resource representation`() {
            every { bookCollection.get(id_cleanCode) } returns Mono.just(record_cleanCode)

            getBook(id_cleanCode)
                .expectStatus().isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().json {
                    """
                    {
                        "id": "$id_cleanCode",
                        "title": "Clean Code",
                        "isbn": "9780132350884"
                    }
                    """
                }
                .andDocument("by-id/get/ok")
        }

        @Test
        fun `getting an unknown book by its id responds with status 404`() {
            every { bookCollection.get(id_cleanCode) } returns Mono.error(BookRecordNotFoundException(id_cleanCode))

            getBook(id_cleanCode)
                .expectStatus().isNotFound
                .expectBody()
                .andDocument("by-id/get/not-found")
                .isEmpty
        }

    }

    @Nested
    @DisplayName("DELETE /default-api/books/{id}")
    inner class DeleteById {

        fun deleteBook(id: UUID) = webTestClient.delete()
            .uri("/default-api/books/$id")
            .exchange()

        @Test
        fun `deleting a book by its id returns status 204`() {
            every { bookCollection.delete(id_cleanCode) } returns Mono.empty()

            deleteBook(id_cleanCode)
                .expectStatus().isNoContent
                .expectBody()
                .andDocument("by-id/delete/no-content")
                .isEmpty
        }

    }

    fun WebTestClient.BodyContentSpec.json(supplier: () -> String) = json(supplier())

    fun WebTestClient.BodyContentSpec.andDocument(identifier: String) =
        consumeWith(document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))

}
