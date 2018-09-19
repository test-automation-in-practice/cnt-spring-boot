package provider

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.willReturn
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import provider.books.Book
import provider.books.BookDataStore
import provider.books.BookRecord
import java.util.*

@Provider("provider")
@PactFolder("src/test/pacts")
@VerificationReports("console")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension::class, PactVerificationInvocationContextProvider::class)
internal class ContractTest {

    @MockBean lateinit var dataStore: BookDataStore

    @BeforeEach fun setTarget(context: PactVerificationContext, @LocalServerPort port: Int) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate fun `consumer contract tests`(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("Getting book with any ID returns Clean Code")
    fun anyBookByIdRequestReturnsCleanCode() {
        val cleanCodeRecord = BookRecord(UUID.randomUUID(), Book(
                isbn = "9780132350884",
                title = "Clean Code",
                description = "Lorem Ipsum ...",
                authors = listOf("Robert C. Martin", "Dean Wampler"),
                numberOfPages = 464
        ))
        given { dataStore.getById(any()) } willReturn { cleanCodeRecord }
    }

}