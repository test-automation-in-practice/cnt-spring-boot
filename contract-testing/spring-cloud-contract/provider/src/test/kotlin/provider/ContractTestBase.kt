package provider

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import provider.books.Book
import provider.books.BookRecord
import provider.books.Library
import java.util.UUID

// Base class for all kinds of generated contract test classes.
// Bootstrap a Spring Boot application context with a mock implementation of the Library interface.

@TestInstance(PER_CLASS)
@SpringBootTest(classes = [ContractTestConfiguration::class])
class ContractTestBase {

    @BeforeAll
    fun bindContractsToApplicationContext(@Autowired context: WebApplicationContext) {
        // Simple setup of RestAssuredMockMvc using the current application context.
        // Without the spring-security-test dependency this would need extra steps to enable security during testing.
        // Example: (https://github.com/spring-cloud-samples/spring-cloud-contract-samples/tree/main/producer_security)
        RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.webAppContextSetup(context))
    }

    @BeforeAll
    fun initMocks(@Autowired library: Library) {
        // Simply stubs the mock Library with all known books for their respective IDs.
        ContractTestData.allBooksRecords
            .forEach { bookRecord ->
                every { library.findById(bookRecord.id) } returns bookRecord
            }
    }

}

@ComponentScan
@EnableAutoConfiguration
private class ContractTestConfiguration {
    @Bean
    fun mockLibrary(): Library = mockk()
}

object ContractTestData {
    val cleanCode = BookRecord(
        id = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4"),
        book = Book(
            isbn = "9780132350884",
            title = "Clean Code",
            description = "Lorem Ipsum ...",
            authors = listOf("Robert C. Martin", "Dean Wampler"),
            numberOfPages = 464
        )
    )

    val allBooksRecords = listOf(cleanCode)
}
