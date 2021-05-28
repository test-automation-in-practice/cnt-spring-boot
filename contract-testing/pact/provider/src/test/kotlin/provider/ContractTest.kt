package provider

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import provider.books.Book
import provider.books.BookDataStore
import provider.books.BookRecord
import java.util.*

private class AdditionalBeans {
    @Primary @Bean fun bookDataStore(): BookDataStore = mockk()
}

@Provider("provider")
@PactFolder("src/test/pacts")
@VerificationReports("console")
@SpringBootTest(
    classes = [Application::class, AdditionalBeans::class],
    webEnvironment = RANDOM_PORT
)
@TestInstance(PER_CLASS) // PACT needs this ... for some reason ...
@ExtendWith(PactVerificationInvocationContextProvider::class)
internal class ContractTest(
    @Autowired val bookDataStore: BookDataStore
) {

    @BeforeEach fun resetMocks() = clearAllMocks()
    @BeforeEach fun setTarget(context: PactVerificationContext, @LocalServerPort port: Int) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate fun `consumer contract tests`(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("Getting book with any ID returns Clean Code")
    fun anyBookByIdRequestReturnsCleanCode() {
        val cleanCodeRecord = BookRecord(
            id = UUID.randomUUID(),
            book = Book(
                isbn = "9780132350884",
                title = "Clean Code",
                description = "Lorem Ipsum ...",
                authors = listOf("Robert C. Martin", "Dean Wampler"),
                numberOfPages = 464
            )
        )
        every { bookDataStore.getById(any()) } returns cleanCodeRecord
    }

}