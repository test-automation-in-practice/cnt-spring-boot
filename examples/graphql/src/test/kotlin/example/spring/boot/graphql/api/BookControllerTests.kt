package example.spring.boot.graphql.api

import com.ninjasquad.springmockk.MockkBean
import example.spring.boot.graphql.business.BookCollection
import example.spring.boot.graphql.business.Examples.book_projectHailMary
import example.spring.boot.graphql.business.Examples.book_theMartian
import example.spring.boot.graphql.business.Examples.id_projectHailMary
import example.spring.boot.graphql.business.Examples.id_theMartian
import example.spring.boot.graphql.business.Examples.record_projectHailMary
import example.spring.boot.graphql.business.Examples.record_theMartian
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
        every { collection.getAll() } returns listOf(record_theMartian, record_projectHailMary)
        every { collection.get(any()) } returns null
        every { collection.get(id_theMartian) } returns record_theMartian
        every { collection.get(id_projectHailMary) } returns record_projectHailMary
        every { collection.delete(any()) } returns false
        every { collection.delete(id_theMartian) } returns true
        every { collection.delete(id_projectHailMary) } returns true
    }

    @Test
    fun `addBook returns persisted book record`() {
        @Language("graphql")
        val document = """
            mutation {
              addBook(title: "Project Hail Mary", isbn: "9780593135204") {
                id
              }
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("addBook").matchesJsonStrictly("""{ "id": "7d823198-2ef3-41a6-b780-29ba6723d8c9" }""")
    }

    @Test
    fun `getAllBooks returns all books`() {
        @Language("graphql")
        val document = """
            query {
              getAllBooks {
                title
              }
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("getAllBooks").matchesJsonStrictly(
                """[{ "title": "The Martian" }, { "title": "Project Hail Mary" }]"""
            )
    }

    @Test
    fun `getBookById returns book if found`() {
        @Language("graphql")
        val document = """
            query {
              getBookById(id: "$id_theMartian") {
                title
              }
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("getBookById").matchesJsonStrictly(
                """{"title": "The Martian" }"""
            )
    }

    @Test
    fun `getBookById returns null if not found`() {
        @Language("graphql")
        val document = """
            query {
              getBookById(id: "${randomUUID()}") {
                title
              }
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("getBookById").valueIsNull()
    }

    @Test
    fun `deleteBookById returns true if deleted`() {
        @Language("graphql")
        val document = """
            mutation {
              deleteBookById(id: "$id_projectHailMary")
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("deleteBookById").entity(Boolean::class.java).isEqualTo(true)
    }

    @Test
    fun `deleteBookById returns false if not deleted`() {
        @Language("graphql")
        val document = """
            mutation {
              deleteBookById(id: "${randomUUID()}")
            }
            """

        graphQlTester.document(document).execute()
            .errors().verify()
            .path("deleteBookById").entity(Boolean::class.java).isEqualTo(false)
    }
}
