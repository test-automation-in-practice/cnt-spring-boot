package example.spring.boot.graphql

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.BookRecord
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
import example.spring.boot.graphql.persistence.BookRecordRepository
import example.spring.boot.graphql.utils.GraphQLRequestSnippet
import example.spring.boot.graphql.utils.RestDocsWebTestClientCustomizer
import io.mockk.every
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.IdGenerator
import java.util.UUID

@AutoConfigureWebTestClient
@MockkBean(types = [IdGenerator::class])
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(RestDocsWebTestClientCustomizer::class)
@AutoConfigureRestDocs("build/generated-snippets")
internal class ApplicationAcceptanceTest(
    @Autowired val idGenerator: IdGenerator,
    @Autowired val repository: BookRecordRepository,
    @Autowired val client: WebTestClient
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `adding a book`() {
        defineNextGeneratedId("b3fc0be8-463e-4875-9629-67921a1e00f4")
        testGraphQLInteraction(
            documentationId = "books/add/created",
            graphqlQuery = """
                mutation {
                  addBook(title: "The Martian", isbn: "9780804139021") {
                    id
                    title
                    isbn
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "addBook": {
                      "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                      "title": "The Martian",
                      "isbn": "9780804139021"
                    }
                  }
                }                
                """
        )
    }

    @Test
    fun `finding books by title`() {
        defineAvailableBookRecords(record_theMartian)
        testGraphQLInteraction(
            documentationId = "books/find/title/found",
            graphqlQuery = """
                query {
                  findBooks(query: { title: "martian" }) {
                    id
                    title
                    isbn
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "findBooks": [
                      {
                        "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                        "title": "The Martian",
                        "isbn": "9780804139021"
                      }
                    ]
                  }
                }                
                """
        )
    }

    @Test
    fun `getting all books`() {
        defineAvailableBookRecords(record_theMartian, record_projectHailMary)
        testGraphQLInteraction(
            documentationId = "books/get-all/found",
            graphqlQuery = """
                query {
                  getAllBooks(pagination: {index: 0, size: 10}) {
                    content {
                      id
                      title
                      isbn
                    }
                    index
                    size
                    totalPages
                    totalElements
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "getAllBooks": {
                      "content": [
                        {
                          "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9",
                          "title": "Project Hail Mary",
                          "isbn": "9780593135204"
                        },
                        {
                          "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                          "title": "The Martian",
                          "isbn": "9780804139021"
                        }
                      ],
                      "index": 0,
                      "size": 10,
                      "totalPages": 1,
                      "totalElements": 2
                    }
                  }
                }                
                """
        )
    }

    @Test
    fun `getting a book by id`() {
        defineAvailableBookRecords(record_projectHailMary)
        testGraphQLInteraction(
            documentationId = "books/get-by-id/found",
            graphqlQuery = """
                query {
                  getBookById(id: "7d823198-2ef3-41a6-b780-29ba6723d8c9") {
                    id
                    title
                    isbn
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "getBookById": {
                      "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9",
                      "title": "Project Hail Mary",
                      "isbn": "9780593135204"
                    }
                  }
                }                
                """
        )
    }

    @Test
    fun `delete a book by id`() {
        defineAvailableBookRecords(record_theMartian)
        testGraphQLInteraction(
            documentationId = "books/delete-by-id/deleted",
            graphqlQuery = """
                mutation {
                  deleteBookById(id: "b3fc0be8-463e-4875-9629-67921a1e00f4")
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "deleteBookById": true
                  }
                }                
                """
        )
    }

    fun defineNextGeneratedId(id: String) {
        every { idGenerator.generateId() } returns UUID.fromString(id)
    }

    fun defineAvailableBookRecords(vararg records: BookRecord) =
        records.forEach(repository::save)

    private fun testGraphQLInteraction(
        documentationId: String,
        @Language("graphql") graphqlQuery: String,
        @Language("json") expectedResponse: String
    ) {
        val actualResponse = client.post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue(mapOf("query" to graphqlQuery))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .andDocument(documentationId)
            .returnResult()
            .responseBody!!
            .let(::String)

        assertEquals(expectedResponse, actualResponse, STRICT)
    }

    fun WebTestClient.BodyContentSpec.andDocument(identifier: String) =
        consumeWith(
            document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                GraphQLRequestSnippet,
            )
        )
}
