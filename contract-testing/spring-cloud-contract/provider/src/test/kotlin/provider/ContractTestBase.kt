package provider

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import provider.books.Book
import provider.books.BookRecord
import provider.books.Library
import java.util.UUID

@WithMockUser
@TestInstance(PER_CLASS)
@SpringBootTest(classes = [ContractTestConfiguration::class])
class ContractTestBase {

    @BeforeAll
    fun bindContractsToApplicationContext(@Autowired context: WebApplicationContext) {
        standaloneSetup(webAppContextSetup(context))
    }

    @BeforeAll
    fun initMocks(@Autowired library: Library) {
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
