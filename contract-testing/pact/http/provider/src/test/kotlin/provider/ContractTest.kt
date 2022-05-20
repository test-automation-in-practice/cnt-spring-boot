package provider

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.VerificationReports
import au.com.dius.pact.provider.junitsupport.loader.PactFolder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.apache.hc.core5.http.HttpRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.encodeBasicAuth
import provider.books.Book
import provider.books.BookRecord
import provider.books.Library
import java.util.*
import kotlin.text.Charsets.UTF_8

// Example of provider-side contract test.

@TestInstance(PER_CLASS) // PACT needs test instance per class ... for some reason ...
@Provider("provider") // load all contracts for the provider named "provider"
@PactFolder("src/test/pacts") // define where contract files come from - in this example they are part of the repository
@VerificationReports("console") // define what kind of reporting of the test should be done
@SpringBootTest(classes = [ContractTestConfiguration::class], webEnvironment = RANDOM_PORT)
@ExtendWith(PactVerificationInvocationContextProvider::class)
internal class ContractTest(
    @Autowired val library: Library
) {

    @BeforeEach
    fun resetMocks() = clearAllMocks()

    // Configure PACT to use the randomly assigned port of our running test application
    @BeforeEach
    fun setTarget(context: PactVerificationContext, @LocalServerPort port: Int) {
        context.target = HttpTestTarget("localhost", port)
    }

    // Trigger verification for each interaction of each relevant contract file
    // Also add "Authorization" header for each interaction's request.
    @TestTemplate
    fun `consumer contract tests`(context: PactVerificationContext, request: HttpRequest) {
        request.setHeader(AUTHORIZATION, basicAuthHeaderFor("user"))
        context.verifyInteraction()
    }

    // Callback method for defined provider state of one of the contracts
    @State("Getting book with any ID returns Clean Code")
    fun anyBookByIdRequestReturnsCleanCode() {
        every { library.findById(any()) } returns cleanCode()
    }

    // Callback method for defined provider state of another more specific contract
    @State("Getting book with ID [{id}] returns Clean Code")
    fun bookByIdRequestWithSpecificIdReturnsCleanCode(parameters: Map<String, String>) {
        val id = id(parameters.getValue("id"))
        every { library.findById(id) } returns cleanCode(id)
    }

    private fun basicAuthHeaderFor(username: String) =
        "Basic ${encodeBasicAuth(username, username.reversed(), UTF_8)}"

    private fun cleanCode(id: UUID = UUID.randomUUID()) = BookRecord(
        id = id,
        book = Book(
            isbn = "9780132350884",
            title = "Clean Code",
            description = "Lorem Ipsum ...",
            authors = listOf("Robert C. Martin", "Dean Wampler"),
            numberOfPages = 464
        )
    )

    private fun id(value: String) = UUID.fromString(value)
}

@ComponentScan
@EnableAutoConfiguration
private class ContractTestConfiguration {
    @Bean
    fun mockLibrary(): Library = mockk()
}
