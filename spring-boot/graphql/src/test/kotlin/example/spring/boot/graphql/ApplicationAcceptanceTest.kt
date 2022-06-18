package example.spring.boot.graphql

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.Examples.id_theMartian
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
import example.spring.boot.graphql.persistence.BookRecordRepository
import example.spring.boot.graphql.utils.GraphQLRequestSnippet
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
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
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentationConfigurer
import org.springframework.util.IdGenerator

@MockkBean(IdGenerator::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest(
    @Autowired val idGenerator: IdGenerator,
    @Autowired val repository: BookRecordRepository
) {

    @RegisterExtension
    val restDocumentation = RestDocumentationExtension("build/generated-snippets/books")

    lateinit var documentationConfigurationFilter: RestAssuredRestDocumentationConfigurer

    @BeforeEach
    fun setupRestDocs(contextProvider: RestDocumentationContextProvider) {
        documentationConfigurationFilter = documentationConfiguration(contextProvider)
    }

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        RestAssured.port = port
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `adding a book`() {
        every { idGenerator.generateId() } returns id_theMartian

        testGraphQLInteraction(
            query = """
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
                """,
            identifier = "add/created"
        )
    }

    @Test
    fun `getting all books`() {
        repository.save(record_theMartian)
        repository.save(record_projectHailMary)

        testGraphQLInteraction(
            query = """
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
                """,
            identifier = "get-all/found"
        )
    }

    @Test
    fun `getting a book by id`() {
        repository.save(record_projectHailMary)

        testGraphQLInteraction(
            query = """
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
                """,
            identifier = "get-by-id/found"
        )
    }

    @Test
    fun `delete a book by id`() {
        repository.save(record_theMartian)

        testGraphQLInteraction(
            query = """
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
                """,
            identifier = "delete-by-id/deleted"
        )
    }

    private fun testGraphQLInteraction(
        @Language("graphql") query: String,
        @Language("json") expectedResponse: String,
        identifier: String
    ) {
        val actualResponse = Given {
            document(identifier)
            header("Content-Type", "application/json")
            body(mapOf("query" to query))
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
            filter(documentationConfigurationFilter)
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
