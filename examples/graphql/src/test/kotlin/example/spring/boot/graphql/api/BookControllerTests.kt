package example.spring.boot.graphql.api

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.BookCollection
import example.spring.boot.graphql.business.Examples.book_projectHailMary
import example.spring.boot.graphql.business.Examples.book_theMartian
import example.spring.boot.graphql.business.Examples.id_projectHailMary
import example.spring.boot.graphql.business.Examples.id_theMartian
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
import example.spring.boot.graphql.business.Pagination
import example.spring.boot.graphql.business.pageOf
import io.mockk.every
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester
import java.util.UUID.randomUUID

@GraphQlTest(BookController::class)
@MockkBean(BookCollection::class)
internal class BookControllerTests(
    @Autowired val graphQlTester: GraphQlTester,
    @Autowired val collection: BookCollection
) {

    @BeforeEach
    fun stubDefaultBehaviour() {
        every { collection.add(book_theMartian) } returns record_theMartian
        every { collection.add(book_projectHailMary) } returns record_projectHailMary
        every { collection.get(any()) } returns null
        every { collection.get(id_theMartian) } returns record_theMartian
        every { collection.get(id_projectHailMary) } returns record_projectHailMary
        every { collection.delete(any()) } returns false
        every { collection.delete(id_theMartian) } returns true
        every { collection.delete(id_projectHailMary) } returns true
    }

    @Test
    fun `addBook returns persisted book record`() {
        executeAndExpect(
            document = """
                mutation {
                  addBook(title: "Project Hail Mary", isbn: "9780593135204") {
                    id
                    isbn
                    title
                  }
                }
                """,
            response = """
                {
                  "data": {
                    "addBook": {
                      "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9",
                      "isbn": "9780593135204",
                      "title": "Project Hail Mary"
                    }
                  }
                }
                """
        )
    }

    @Test
    fun `addBook returns error for invalid ISBN`() {
        executeAndExpect(
            document = """
                mutation {
                  addBook(title: "Project Hail Mary", isbn: "12-9780593135204") {
                    id
                  }
                }
                """,
            response = """
                {
                  "errors": [
                    {
                      "message": "Invalid value [12-9780593135204] for 'isbn' in '$'",
                      "extensions": {
                        "classification": "ValidationError"
                      }
                    }
                  ],
                  "data": { "addBook": null }
                }
                """,
            strict = false
        )
    }

    @Test
    fun `getAllBooks returns first page of all books`() {
        every { collection.getAll(any()) } returns pageOf(record_theMartian, record_projectHailMary)
        executeAndExpect(
            document = """
                query {
                  getAllBooks {
                    content {
                      id
                      isbn
                      title
                    }
                    index
                    size
                    totalPages
                    totalElements
                  }
                }
                """,
            response = """
                {
                  "data": {
                    "getAllBooks": {
                      "content": [
                        {
                          "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                          "isbn": "9780804139021",
                          "title": "The Martian"
                        },
                        {
                          "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9",
                          "isbn": "9780593135204",
                          "title": "Project Hail Mary"
                        }
                      ],
                      "index": 0,
                      "size": 25,
                      "totalPages": 1,
                      "totalElements": 2
                    }
                  }
                }
                """
        )
    }

    @Test
    fun `getAllBooks with pagination returns that page of books`() {
        every { collection.getAll(Pagination(index = 1, size = 2)) } returns
                pageOf(index = 1, size = 2, totalPages = 1, totalElements = 2)

        executeAndExpect(
            document = """
                query {
                  getAllBooks(pagination: {index: 1, size: 2}) {
                    index
                    size
                    totalPages
                    totalElements
                  }
                }
                """,
            response = """
                {
                  "data": {
                    "getAllBooks": {
                      "index": 1,
                      "size": 2,
                      "totalPages": 1,
                      "totalElements": 2
                    }
                  }
                }
                """
        )
    }

    @Test
    fun `getAllBooks with invalid page index returns client error`() {
        executeAndExpect(
            strict = false,
            document = """
                query {
                  getAllBooks(pagination: {index: -1}) {
                    index
                  }
                }
                """,
            response = """
                {
                  "errors": [
                    {
                      "message": "Validation error (WrongType@[getAllBooks]) : argument 'pagination.index' with value 'IntValue{value=-1}' is not a valid 'PageIndex' - Value IntValue{value=-1} is not in range: 0..10000",
                      "extensions": {
                        "classification": "ValidationError"
                      }
                    }
                  ]
                }
                """
        )
    }

    @Test
    fun `getAllBooks with invalid page size returns client error`() {
        executeAndExpect(
            strict = false,
            document = """
                query {
                  getAllBooks(pagination: {size: 0}) {
                    index
                  }
                }
                """,
            response = """
                {
                  "errors": [
                    {
                      "message": "Validation error (WrongType@[getAllBooks]) : argument 'pagination.size' with value 'IntValue{value=0}' is not a valid 'PageSize' - Value IntValue{value=0} is not in range: 1..250",
                      "extensions": {
                        "classification": "ValidationError"
                      }
                    }
                  ]
                }
                """
        )
    }

    @Test
    fun `getBookById returns book if found`() {
        executeAndExpect(
            document = """
                query {
                  getBookById(id: "b3fc0be8-463e-4875-9629-67921a1e00f4") {
                    id
                    isbn
                    title
                  }
                }
                """,
            response = """
                {
                  "data": {
                    "getBookById": {
                      "id": "b3fc0be8-463e-4875-9629-67921a1e00f4",
                      "isbn": "9780804139021",
                      "title": "The Martian"
                    }
                  }
                }
                """
        )
    }

    @Test
    fun `getBookById returns null if not found`() {
        executeAndExpect(
            document = """
                query {
                  getBookById(id: "${randomUUID()}") {
                    title
                  }
                }
                """,
            response = """
              {
                "data": {
                  "getBookById": null
                }
              }  
              """
        )
    }

    @Test
    fun `deleteBookById returns true if deleted`() {
        executeAndExpect(
            document = """mutation { deleteBookById(id: "7d823198-2ef3-41a6-b780-29ba6723d8c9") }""",
            response = """{"data":{"deleteBookById":true}}"""
        )
    }

    @Test
    fun `deleteBookById returns false if not deleted`() {
        executeAndExpect(
            document = """mutation { deleteBookById(id: "${randomUUID()}") }""",
            response = """{"data":{"deleteBookById":false}}"""
        )
    }

    private fun executeAndExpect(
        @Language("graphql") document: String,
        @Language("json") response: String,
        strict: Boolean = true
    ) {
        graphQlTester.document(document).execute()
            .errors().filter { true }.verify()
            .path("$")
            .apply { if (strict) matchesJsonStrictly(response) else matchesJson(response) }
    }
}
