package example.spring.boot.basics

import example.spring.boot.basics.books.enrichment.BookInformation
import example.spring.boot.basics.books.enrichment.BookInformationSource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest
internal class ApplicationTest {

    @Test
    fun `application context can be loaded`() {
        // nothing to do
    }

}

@Component
class DummyBookInformationSource : BookInformationSource {
    override fun getBookInformation(isbn: String): BookInformation? = null
}
