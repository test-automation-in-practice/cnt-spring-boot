package example.spring.boot.webflux.api.hateoas

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.webflux.business.BookCollection
import example.spring.boot.webflux.business.BookRecordNotFoundException
import example.spring.boot.webflux.business.Examples.book_cleanCode
import example.spring.boot.webflux.business.Examples.id_cleanArchitecture
import example.spring.boot.webflux.business.Examples.id_cleanCode
import example.spring.boot.webflux.business.Examples.record_cleanArchitecture
import example.spring.boot.webflux.business.Examples.record_cleanCode
import io.mockk.every
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@MockkBean(BookCollection::class)
@WebFluxTest(HateoasBookController::class)
@Import(BookRepresentationAssembler::class)
@AutoConfigureRestDocs("build/generated-snippets/hateoas/books")
internal class HateoasBookControllerTest(
    @Autowired val bookCollection: BookCollection,
    @Autowired val webTestClient: WebTestClient
) {

    @Nested
    @DisplayName("POST /hateoas-api/books")
    inner class Post {

        fun postBook(@Language("json") body: String) = webTestClient.post()
            .uri("/hateoas-api/books")
            .contentType(APPLICATION_JSON)
            .bodyValue(body)
            .exchange()

        @Test
        fun `posting a book adds it to the library and returns resource representation`() {
            every { bookCollection.add(book_cleanCode) } returns Mono.just(record_cleanCode)

            postBook("""{ "title": "Clean Code", "isbn": "9780132350884" }""")
                .expectStatus().isCreated
                .expectHeader().contentType(HAL_JSON)
                .expectBody().strictJson(
                    """
                    {
                      "title": "Clean Code",
                      "isbn": "9780132350884",
                      "_links": {
                        "self": {
                          "href": "http://localhost:8080/hateoas-api/books/$id_cleanCode"
                        }
                      }
                    }
                    """
                )
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
    @DisplayName("GET /hateoas-api/books")
    inner class Get {

        fun getBooks() = webTestClient.get()
            .uri("/hateoas-api/books")
            .exchange()

        @Test
        fun `getting all books returns their resource representation as a resource list`() {
            every { bookCollection.getAll() } returns Flux.just(record_cleanCode, record_cleanArchitecture)

            getBooks()
                .expectStatus().isOk
                .expectHeader().contentType(HAL_JSON)
                .expectBody().strictJson(
                    """
                    {
                      "_embedded": {
                        "books": [
                          {
                            "title": "Clean Code",
                            "isbn": "9780132350884",
                            "_links": {
                              "self": {
                                "href": "http://localhost:8080/hateoas-api/books/$id_cleanCode"
                              }
                            }
                          },
                          {
                            "title": "Clean Architecture",
                            "isbn": "9780134494166",
                            "_links": {
                              "self": {
                                "href": "http://localhost:8080/hateoas-api/books/$id_cleanArchitecture"
                              }
                            }
                          }
                        ]
                      },
                      "_links": {
                        "self": {
                          "href": "http://localhost:8080/hateoas-api/books"
                        }
                      }
                    }
                    """
                )
                .andDocument("get/ok_found")
        }

        @Test
        fun `getting all books when there are none returns empty resource list`() {
            every { bookCollection.getAll() } returns Flux.empty()

            getBooks()
                .expectStatus().isOk
                .expectHeader().contentType(HAL_JSON)
                .expectBody().strictJson(
                    """
                    {
                      "_links": {
                        "self": {
                          "href": "http://localhost:8080/hateoas-api/books"
                        }
                      }
                    }
                    """
                )
                .andDocument("get/ok_empty")
        }

    }

    @Nested
    @DisplayName("GET /hateoas-api/books/{id}")
    inner class GetById {

        fun getBook(id: UUID) = webTestClient.get()
            .uri("/hateoas-api/books/$id")
            .exchange()

        @Test
        fun `getting a book by its id returns resource representation`() {
            every { bookCollection.get(id_cleanCode) } returns Mono.just(record_cleanCode)

            getBook(id_cleanCode)
                .expectStatus().isOk
                .expectHeader().contentType(HAL_JSON)
                .expectBody().strictJson(
                    """
                    {
                      "title": "Clean Code",
                      "isbn": "9780132350884",
                      "_links": {
                        "self": {
                          "href": "http://localhost:8080/hateoas-api/books/$id_cleanCode"
                        }
                      }
                    }
                    """
                )
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
    @DisplayName("DELETE /hateoas-api/books/{id}")
    inner class DeleteById {

        fun deleteBook(id: UUID) = webTestClient.delete()
            .uri("/hateoas-api/books/$id")
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

    fun WebTestClient.BodyContentSpec.strictJson(@Language("json") json: String) = json(json, STRICT)

    fun WebTestClient.BodyContentSpec.andDocument(identifier: String) =
        consumeWith(
            WebTestClientRestDocumentation.document(
                identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
            )
        )

}
