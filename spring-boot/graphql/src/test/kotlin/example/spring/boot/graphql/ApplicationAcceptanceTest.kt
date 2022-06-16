package example.spring.boot.graphql

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.Examples.id_theMartian
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
import example.spring.boot.graphql.persistence.BookRecordRepository
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.util.IdGenerator

@MockkBean(IdGenerator::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class ApplicationAcceptanceTest(
    @Autowired val idGenerator: IdGenerator,
    @Autowired val repository: BookRecordRepository
) {

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
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "addBook": {
                      "id": "b3fc0be8-463e-4875-9629-67921a1e00f4"
                    }
                  }
                }                
                """
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
                    title
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "getAllBooks": [
                      { "title": "The Martian" },
                      { "title": "Project Hail Mary" }
                    ]
                  }
                }                
                """
        )
    }

    @Test
    fun `getting a book by id`() {
        repository.save(record_projectHailMary)

        testGraphQLInteraction(
            query = """
                query {
                  getBookById(id: "7d823198-2ef3-41a6-b780-29ba6723d8c9") {
                    title
                  }
                }
                """,
            expectedResponse = """
                {
                  "data": {
                    "getBookById": {
                      "title": "Project Hail Mary"
                    }
                  }
                }                
                """
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
                """
        )
    }

    private fun testGraphQLInteraction(
        @Language("graphql") query: String,
        @Language("json") expectedResponse: String
    ) {
        val actualResponse = Given {
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

}
