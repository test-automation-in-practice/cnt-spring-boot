package example.spring.boot.graphql

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.BookRecord
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
import example.spring.boot.graphql.persistence.BookRecordRepository
import example.spring.boot.graphql.utils.GraphQLRequestSnippet
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.filter.Filter
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.util.IdGenerator
import java.util.UUID

@MockkBean(IdGenerator::class)
@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest(
    @Autowired val idGenerator: IdGenerator,
    @Autowired val repository: BookRecordRepository
) {

    lateinit var documentationConfiguration: Filter

    @BeforeEach
    fun setup(@LocalServerPort port: Int, contextProvider: RestDocumentationContextProvider) {
        RestAssured.port = port
        documentationConfiguration = documentationConfiguration(contextProvider)
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
    fun `getting all books`() {
        defineAvailableBookRecords(record_theMartian, record_projectHailMary)
        testGraphQLInteraction(
            documentationId = "books/get-all/found",
            graphqlQuery = """
                query {
                  getAllBooks {
                    id
                    title
                    isbn
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "getAllBooks": [
                      {
                        "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                        "title": "The Martian",
                        "isbn": "9780804139021"
                      },
                      {
                        "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9",
                        "title": "Project Hail Mary",
                        "isbn": "9780593135204"
                      }
                    ]
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
        val actualResponse = Given {
            document(documentationId)
            header("Content-Type", "application/json")
            body(mapOf("query" to graphqlQuery))
        } When {
            post("/graphql")
        } Then {
            statusCode(200)
            contentType("application/json")
        } Extract {
            body().asString()
        }
        assertEquals(expectedResponse, actualResponse, STRICT)
    }

    private fun RequestSpecification.document(identifier: String): RequestSpecification =
        apply {
            filter(documentationConfiguration)
            filter(
                document(
                    /* identifier = */ identifier,
                    /* requestPreprocessor = */ preprocessRequest(prettyPrint()),
                    /* responsePreprocessor = */ preprocessResponse(prettyPrint()),
                    /* ...snippets = */ GraphQLRequestSnippet
                )
            )
        }
}
